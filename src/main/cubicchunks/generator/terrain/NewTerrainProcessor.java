/*
 *  This file is part of Cubic Chunks, licensed under the MIT License (MIT).
 *
 *  Copyright (c) 2014 Tall Worlds
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package cubicchunks.generator.terrain;

import java.util.Random;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import cubicchunks.generator.builder.BasicBuilder;
import cubicchunks.generator.builder.IBuilder;
import static cubicchunks.generator.terrain.GlobalGeneratorConfig.X_SECTIONS;
import static cubicchunks.generator.terrain.GlobalGeneratorConfig.X_SECTION_SIZE;
import static cubicchunks.generator.terrain.GlobalGeneratorConfig.Y_SECTIONS;
import static cubicchunks.generator.terrain.GlobalGeneratorConfig.Y_SECTION_SIZE;
import static cubicchunks.generator.terrain.GlobalGeneratorConfig.Z_SECTIONS;
import static cubicchunks.generator.terrain.GlobalGeneratorConfig.Z_SECTION_SIZE;
import static cubicchunks.generator.terrain.GlobalGeneratorConfig.maxElev;
import cubicchunks.world.cube.Cube;
import cubicchunks.world.ICubeCache;

public class NewTerrainProcessor extends AbstractTerrainProcessor3dNoise {
	private Biome[] biomes;

	private final Random rand;

	private double biomeVolatility;
	private double biomeHeight;

	private final int maxSmoothRadius;
	private final int maxSmoothDiameter;

	private final double[][] noiseArrayHeight;

	private final double[] nearBiomeWeightArray;

	private final BasicBuilder builderHeight;

	private static final int octaves = 16;

	public NewTerrainProcessor(String name, WorldServer worldServer, ICubeCache cache, int batchSize) {
		super(name, worldServer, cache, batchSize);

		this.maxSmoothRadius = 2 * (int) (maxElev / 64);
		this.maxSmoothDiameter = maxSmoothRadius * 2 + 1;

		this.biomes = null;

		this.rand = new Random(this.worldServer.getSeed());

		this.noiseArrayHeight = new double[X_SECTIONS][Z_SECTIONS];

		this.nearBiomeWeightArray = new double[maxSmoothDiameter * maxSmoothDiameter];

		for (int x = -maxSmoothRadius; x <= maxSmoothRadius; x++) {
			for (int z = -maxSmoothRadius; z <= maxSmoothRadius; z++) {
				final double f1 = 10.0F / Math.sqrt(x * x + z * z + 0.2F);
				this.nearBiomeWeightArray[(x + maxSmoothRadius + (z + maxSmoothRadius) * maxSmoothDiameter)] = f1;
			}
		}

		double freq = 200.0 / Math.pow(2, 10) / (maxElev / 64);

		builderHeight = new BasicBuilder();
		builderHeight.setSeed(rand.nextInt());
		builderHeight.setOctaves(10);
		builderHeight.setMaxElev(8);
		builderHeight.setFreq(freq);
		builderHeight.build();
	}

	@Override
	protected IBuilder createHighBuilder() {
		Random rand = new Random(worldServer.getSeed() * 2);
		double freq = 684.412D / Math.pow(2, octaves) / (maxElev / 64.0);

		BasicBuilder builderHigh = new BasicBuilder();
		builderHigh.setSeed(rand.nextInt());
		builderHigh.setOctaves(octaves);
		builderHigh.setPersistance(0.5);
		// with 16 octaves probability of getting 1 is too low
		builderHigh.setMaxElev(2);
		builderHigh.setClamp(-1, 1);
		builderHigh.setFreq(freq, freq, freq);
		builderHigh.build();

		return builderHigh;
	}

	@Override
	protected IBuilder createLowBuilder() {
		Random rand = new Random(worldServer.getSeed() * 3);
		double freq = 684.412D / Math.pow(2, octaves) / (maxElev / 64.0);

		BasicBuilder builderLow = new BasicBuilder();
		builderLow.setSeed(rand.nextInt());
		builderLow.setOctaves(octaves);
		builderLow.setPersistance(0.5);
		builderLow.setMaxElev(2);
		builderLow.setClamp(-1, 1);
		builderLow.setFreq(freq, freq, freq);
		builderLow.build();

		return builderLow;
	}

	@Override
	protected IBuilder createAlphaBuilder() {
		Random rand = new Random(worldServer.getSeed() * 4);
		double freq = 8.55515 / Math.pow(2, 8) / (maxElev / 64.0);

		BasicBuilder builderAlpha = new BasicBuilder();
		builderAlpha.setSeed(rand.nextInt());
		builderAlpha.setOctaves(8);
		builderAlpha.setPersistance(0.5);
		builderAlpha.setMaxElev(25.6);
		builderAlpha.setSeaLevel(0.5);
		builderAlpha.setClamp(0, 1);
		builderAlpha.setFreq(freq, freq * 2, freq);
		builderAlpha.build();

		return builderAlpha;
	}

	@Override
	protected void generateTerrainArray(Cube cube) {
		biomes = this.worldServer.dimension.getBiomeManager().getBiomeMap2(biomes,
				cube.getX() * 4 - maxSmoothRadius, cube.getZ() * 4 - maxSmoothRadius,
				X_SECTION_SIZE + maxSmoothDiameter, Z_SECTION_SIZE + maxSmoothDiameter);

		this.fillHeightArray(cube);
		for (int x = 0; x < X_SECTIONS; x++) {
			for (int z = 0; z < Z_SECTIONS; z++) {
				// TODO: Remove addHeight?
				double addHeight = getAddHeight(x, z);
				this.biomeFactor(x, z, addHeight);

				for (int y = 0; y < Y_SECTIONS; y++) {
					final double vol1Low = this.noiseArrayLow[x][y][z];
					final double vol2High = this.noiseArrayHigh[x][y][z];

					final double noiseAlpha = this.noiseArrayAlpha[x][y][z];

					double output = lerp(noiseAlpha, vol1Low, vol2High);

					double heightModifier = this.biomeHeight;
					double volatilityModifier = this.biomeVolatility;

					final double yAbs = (cube.getY() * 16.0 + y * 8.0) / maxElev;
					if (yAbs < heightModifier) {
						// terrain below average biome geight is more flat
						volatilityModifier /= 4.0;
					}

					// NOTE: Multiplication by nonnegative number and addition
					// when using 3d noise effects are the same as with
					// heightmap.

					// make height range lower
					output *= volatilityModifier;
					// height shift
					output += heightModifier;

					// Since in TWM we don't have height limit we could skip it
					// but PLATEAU biomes need it
					int maxYSections = (int) Math.round(maxElev / Y_SECTION_SIZE);
					if (yAbs * maxElev > maxYSections - 4) {
						// TODO: Convert this to work correctly with noise
						// between -1 and 1
						// final double a = ( yAbs - ( maxYSections - 4 ) ) /
						// 3.0F;
						// output = output * ( 1.0D - a ) - 10.0D * a;
					}
					this.rawTerrainArray[x][y][z] = output;
				}
			}
		}
	}

	/**
	 * Calculates biome height and volatility and adds addHeight to result. It
	 * converts vanilla biome values to some more predictable format:
	 * 
	 * biome volatility == 0 will generate flat terrain
	 * 
	 * biome volatility == 0.5 means that max difference between the actual
	 * height and average height is 0.5 of max generation height from sea level.
	 * High volatility will generate overhangs
	 * 
	 * biome height == 0 will generate terrain at sea level
	 * 
	 * biome height == 1 will generate terrain will generate at max generation
	 * height above sea level.
	 * 
	 * Volatility Note: Terrain below biome height has volatility divided by 4,
	 * probably to add some flat terrain to mountanious biomes
	 */
	private void biomeFactor(int x, int z, double addHeight) {
		// Calculate weighted average of nearby biomes height and volatility
		float smoothVolatility = 0.0F;
		float smoothHeight = 0.0F;

		float biomeWeightSum = 0.0F;
		final Biome centerBiomeConfig = this.biomes[(x + this.maxSmoothRadius + (z + this.maxSmoothRadius)
				* (X_SECTION_SIZE + this.maxSmoothDiameter))];
		final int lookRadius = maxSmoothRadius;

		for (int nextX = -lookRadius; nextX <= lookRadius; nextX++) {
			for (int nextZ = -lookRadius; nextZ <= lookRadius; nextZ++) {
				final Biome biome = this.biomes[(x + nextX + this.maxSmoothRadius + (z + nextZ + this.maxSmoothRadius)
						* (X_SECTION_SIZE + this.maxSmoothDiameter))];
				float biomeHeight = biome.height;
				float biomeVolatility = biome.volatility;

				double biomeWeight = this.nearBiomeWeightArray[(nextX + this.maxSmoothRadius + (nextZ + this.maxSmoothRadius)
						* this.maxSmoothDiameter)]
						/ (biomeHeight + 2.0F);

				biomeWeight = Math.abs(biomeWeight);
				if (biomeHeight > centerBiomeConfig.height) {
					// prefer biomes with lower height?
					biomeWeight /= 2.0F;
				}
				smoothVolatility += biomeVolatility * biomeWeight;
				smoothHeight += biomeHeight * biomeWeight;
				
				biomeWeightSum += biomeWeight;
			}
		}

		smoothVolatility /= biomeWeightSum;
		smoothHeight /= biomeWeightSum;

		// Convert from vanilla height/volatility format
		// to something easier to predict
		this.biomeVolatility = smoothVolatility * 0.9 + 0.1;
		this.biomeVolatility *= 4.0 / 3.0;

		// divide everything by 64, then it will be multpllied by maxElev
		// vanilla sea level: 63.75 / 64.00

		// sea level 0.75/64 of height above sea level (63.75 = 63+0.75)
		this.biomeHeight = 0.75 / 64.0;
		this.biomeHeight += smoothHeight * 17.0 / 64.0;
		// TODO: Remove addHeight? it changes the result by at most 1 block
		this.biomeHeight += 0.2 * addHeight * 17.0 / 64.0;
	}

	private void fillHeightArray(Cube cube) {
		int cubeXMin = cube.getX() * (X_SECTION_SIZE - 1);
		int cubeZMin = cube.getZ() * (Z_SECTION_SIZE - 1);

		for (int x = 0; x < X_SECTIONS; x++) {
			int xPos = cubeXMin + x;

			for (int z = 0; z < Z_SECTIONS; z++) {
				int zPos = cubeZMin + z;

				this.noiseArrayHeight[x][z] = builderHeight.getValue(xPos, 0, zPos);

			}
		}
	}

	/**
	 * This method is there only because the code exists in vanilla, it affects
	 * terrain height by at most 1 block (+/-0.425 blocks).
	 * 
	 * In Minecraft beta it was base terrain height, but as of beta 1.8 it
	 * doesn't have any significant effect. It's multiplied 0.2 before it's
	 * used.
	 */
	private double getAddHeight(int x, int z) {
		double noiseHeight = noiseArrayHeight[x][z];

		assert noiseHeight <= 8 && noiseHeight >= -8;

		if (noiseHeight < 0.0D) {
			noiseHeight = -noiseHeight * 0.3D;
		}

		noiseHeight = noiseHeight * 3.0D - 2.0D;

		if (noiseHeight < 0.0D) {
			noiseHeight /= 2.0D;

			if (noiseHeight < -1.0D) {
				noiseHeight = -1.0D;
			}
			noiseHeight /= 1.4D;
			noiseHeight /= 2.0D;

		} else {
			if (noiseHeight > 1.0D) {
				noiseHeight = 1.0D;
			}
			noiseHeight /= 8.0D;
		}
		return noiseHeight;
	}
}
