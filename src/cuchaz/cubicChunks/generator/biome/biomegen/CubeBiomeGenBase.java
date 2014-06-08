/*******************************************************************************
 * Copyright (c) 2014 Jeff Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jeff Martin - initial API and implementation
 ******************************************************************************/
package cuchaz.cubicChunks.generator.biome.biomegen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cuchaz.cubicChunks.generator.populator.WorldGenAbstractTreeCube;
import cuchaz.cubicChunks.generator.populator.WorldGeneratorCube;
import cuchaz.cubicChunks.generator.populator.generators.WorldGenBigTreeCube;
import cuchaz.cubicChunks.generator.populator.generators.WorldGenDoublePlantCube;
import cuchaz.cubicChunks.generator.populator.generators.WorldGenSwampCube;
import cuchaz.cubicChunks.generator.populator.generators.WorldGenTallGrassCube;
import cuchaz.cubicChunks.generator.populator.generators.WorldGenTreesCube;

import cuchaz.cubicChunks.world.Cube;
import cuchaz.magicMojoModLoader.api.events.BuildSizeEvent;
import java.util.List;

public abstract class CubeBiomeGenBase extends net.minecraft.world.biome.BiomeGenBase
{
	private static final Logger logger = LogManager.getLogger();
	protected static final CubeBiomeGenBase.Height defaultBiomeRange = new CubeBiomeGenBase.Height( 0.1F, 0.2F );
	protected static final CubeBiomeGenBase.Height riverRange = new CubeBiomeGenBase.Height( -0.5F, 0.0F );
	protected static final CubeBiomeGenBase.Height oceanRange = new CubeBiomeGenBase.Height( -1.0F, 0.1F );
	protected static final CubeBiomeGenBase.Height deepOceanRange = new CubeBiomeGenBase.Height( -1.8F, 0.1F );
	protected static final CubeBiomeGenBase.Height PlainsRange = new CubeBiomeGenBase.Height( 0.125F, 0.05F );
	protected static final CubeBiomeGenBase.Height taigaRange = new CubeBiomeGenBase.Height( 0.2F, 0.2F );
	protected static final CubeBiomeGenBase.Height hillsRange = new CubeBiomeGenBase.Height( 0.45F, 0.3F );
	protected static final CubeBiomeGenBase.Height plateauRange = new CubeBiomeGenBase.Height( 1.5F, 0.025F );
	protected static final CubeBiomeGenBase.Height extremeHillsRange = new CubeBiomeGenBase.Height( 1.0F, 0.5F );
	protected static final CubeBiomeGenBase.Height beachRange = new CubeBiomeGenBase.Height( 0.0F, 0.025F );
	protected static final CubeBiomeGenBase.Height stoneBeachRange = new CubeBiomeGenBase.Height( 0.1F, 0.8F );
	protected static final CubeBiomeGenBase.Height mushroomIslandRange = new CubeBiomeGenBase.Height( 0.2F, 0.3F );
	protected static final CubeBiomeGenBase.Height swampRange = new CubeBiomeGenBase.Height( -0.2F, 0.1F );

	/** An array of all the biomes, indexed by biome id. */
	private static final CubeBiomeGenBase[] biomeList = new CubeBiomeGenBase[256];

	public static final CubeBiomeGenBase ocean = (new BiomeGenOcean( 0 )).setColor( 112 ).setBiomeName( "Ocean" ).setHeightRange( oceanRange );
	public static final CubeBiomeGenBase plains = (new BiomeGenPlains( 1 )).setColor( 9286496 ).setBiomeName( "Plains" );
	public static final CubeBiomeGenBase desert = (new BiomeGenDesert( 2 )).setColor( 16421912 ).setBiomeName( "Desert" ).setDisableRain().setTemperatureAndRainfall( 2.0F, 0.0F ).setHeightRange( PlainsRange );
	public static final CubeBiomeGenBase extremeHills = (new BiomeGenHills( 3, false )).setColor( 6316128 ).setBiomeName( "Extreme Hills" ).setHeightRange( extremeHillsRange ).setTemperatureAndRainfall( 0.2F, 0.3F );
	public static final CubeBiomeGenBase forest = (new BiomeGenForest( 4, 0 )).setColor( 353825 ).setBiomeName( "Forest" );
	public static final CubeBiomeGenBase taiga = (new BiomeGenTaiga( 5, 0 )).setColor( 747097 ).setBiomeName( "Taiga" ).func_76733_a( 5159473 ).setTemperatureAndRainfall( 0.25F, 0.8F ).setHeightRange( taigaRange );
	public static final CubeBiomeGenBase swampland = (new BiomeGenSwamp( 6 )).setColor( 522674 ).setBiomeName( "Swampland" ).func_76733_a( 9154376 ).setHeightRange( swampRange ).setTemperatureAndRainfall( 0.8F, 0.9F );
	public static final CubeBiomeGenBase river = (new BiomeGenRiver( 7 )).setColor( 255 ).setBiomeName( "River" ).setHeightRange( riverRange );
	public static final CubeBiomeGenBase hell = (new BiomeGenHell( 8 )).setColor( 16711680 ).setBiomeName( "Hell" ).setDisableRain().setTemperatureAndRainfall( 2.0F, 0.0F );

	/** Is the biome used for sky world. */
	public static final CubeBiomeGenBase sky = (new BiomeGenEnd( 9 )).setColor( 8421631 ).setBiomeName( "Sky" ).setDisableRain();

	public static final CubeBiomeGenBase frozenOcean = (new BiomeGenOcean( 10 )).setColor( 9474208 ).setBiomeName( "FrozenOcean" ).setEnableSnow().setHeightRange( oceanRange ).setTemperatureAndRainfall( 0.0F, 0.5F );
	public static final CubeBiomeGenBase frozenRiver = (new BiomeGenRiver( 11 )).setColor( 10526975 ).setBiomeName( "FrozenRiver" ).setEnableSnow().setHeightRange( riverRange ).setTemperatureAndRainfall( 0.0F, 0.5F );
	public static final CubeBiomeGenBase icePlains = (new BiomeGenSnow( 12, false )).setColor( 16777215 ).setBiomeName( "Ice Plains" ).setEnableSnow().setTemperatureAndRainfall( 0.0F, 0.5F ).setHeightRange( PlainsRange );
	public static final CubeBiomeGenBase iceMountains = (new BiomeGenSnow( 13, false )).setColor( 10526880 ).setBiomeName( "Ice Mountains" ).setEnableSnow().setHeightRange( hillsRange ).setTemperatureAndRainfall( 0.0F, 0.5F );
	public static final CubeBiomeGenBase mushroomIsland = (new BiomeGenMushroomIsland( 14 )).setColor( 16711935 ).setBiomeName( "MushroomIsland" ).setTemperatureAndRainfall( 0.9F, 1.0F ).setHeightRange( mushroomIslandRange );
	public static final CubeBiomeGenBase mushroomIslandShore = (new BiomeGenMushroomIsland( 15 )).setColor( 10486015 ).setBiomeName( "MushroomIslandShore" ).setTemperatureAndRainfall( 0.9F, 1.0F ).setHeightRange( beachRange );

	/** Beach biome. */
	public static final CubeBiomeGenBase beach = (new BiomeGenBeach( 16 )).setColor( 16440917 ).setBiomeName( "Beach" ).setTemperatureAndRainfall( 0.8F, 0.4F ).setHeightRange( beachRange );

	/** Desert Hills biome. */
	public static final CubeBiomeGenBase desertHills = (new BiomeGenDesert( 17 )).setColor( 13786898 ).setBiomeName( "DesertHills" ).setDisableRain().setTemperatureAndRainfall( 2.0F, 0.0F ).setHeightRange( hillsRange );

	/** Forest Hills biome. */
	public static final CubeBiomeGenBase forestHills = (new BiomeGenForest( 18, 0 )).setColor( 2250012 ).setBiomeName( "ForestHills" ).setHeightRange( hillsRange );

	/** Taiga Hills biome. */
	public static final CubeBiomeGenBase taigaHills = (new BiomeGenTaiga( 19, 0 )).setColor( 1456435 ).setBiomeName( "TaigaHills" ).func_76733_a( 5159473 ).setTemperatureAndRainfall( 0.25F, 0.8F ).setHeightRange( hillsRange );

	/** Extreme Hills Edge biome. */
	public static final CubeBiomeGenBase extremeHillsEdge = (new BiomeGenHills( 20, true )).setColor( 7501978 ).setBiomeName( "Extreme Hills Edge" ).setHeightRange( extremeHillsRange.func_150775_a() ).setTemperatureAndRainfall( 0.2F, 0.3F );

	/** Jungle biome identifier */
	public static final CubeBiomeGenBase jungle = (new BiomeGenJungle( 21, false )).setColor( 5470985 ).setBiomeName( "Jungle" ).func_76733_a( 5470985 ).setTemperatureAndRainfall( 0.95F, 0.9F );
	public static final CubeBiomeGenBase jungleHills = (new BiomeGenJungle( 22, false )).setColor( 2900485 ).setBiomeName( "JungleHills" ).func_76733_a( 5470985 ).setTemperatureAndRainfall( 0.95F, 0.9F ).setHeightRange( hillsRange );
	public static final CubeBiomeGenBase jungleEdge = (new BiomeGenJungle( 23, true )).setColor( 6458135 ).setBiomeName( "JungleEdge" ).func_76733_a( 5470985 ).setTemperatureAndRainfall( 0.95F, 0.8F );
	public static final CubeBiomeGenBase deepOcean = (new BiomeGenOcean( 24 )).setColor( 48 ).setBiomeName( "Deep Ocean" ).setHeightRange( deepOceanRange );
	public static final CubeBiomeGenBase stoneBeach = (new BiomeGenStoneBeach( 25 )).setColor( 10658436 ).setBiomeName( "Stone Beach" ).setTemperatureAndRainfall( 0.2F, 0.3F ).setHeightRange( stoneBeachRange );
	public static final CubeBiomeGenBase coldBeach = (new BiomeGenBeach( 26 )).setColor( 16445632 ).setBiomeName( "Cold Beach" ).setTemperatureAndRainfall( 0.05F, 0.3F ).setHeightRange( beachRange ).setEnableSnow();
	public static final CubeBiomeGenBase birchForest = (new BiomeGenForest( 27, 2 )).setBiomeName( "Birch Forest" ).setColor( 3175492 );
	public static final CubeBiomeGenBase birchForestHills = (new BiomeGenForest( 28, 2 )).setBiomeName( "Birch Forest Hills" ).setColor( 2055986 ).setHeightRange( hillsRange );
	public static final CubeBiomeGenBase roofedForest = (new BiomeGenForest( 29, 3 )).setColor( 4215066 ).setBiomeName( "Roofed Forest" );
	public static final CubeBiomeGenBase coldTaiga = (new BiomeGenTaiga( 30, 0 )).setColor( 3233098 ).setBiomeName( "Cold Taiga" ).func_76733_a( 5159473 ).setEnableSnow().setTemperatureAndRainfall( -0.5F, 0.4F ).setHeightRange( taigaRange ).func_150563_c( 16777215 );
	public static final CubeBiomeGenBase coldTaigaHills = (new BiomeGenTaiga( 31, 0 )).setColor( 2375478 ).setBiomeName( "Cold Taiga Hills" ).func_76733_a( 5159473 ).setEnableSnow().setTemperatureAndRainfall( -0.5F, 0.4F ).setHeightRange( hillsRange ).func_150563_c( 16777215 );
	public static final CubeBiomeGenBase megaTaiga = (new BiomeGenTaiga( 32, 1 )).setColor( 5858897 ).setBiomeName( "Mega Taiga" ).func_76733_a( 5159473 ).setTemperatureAndRainfall( 0.3F, 0.8F ).setHeightRange( taigaRange );
	public static final CubeBiomeGenBase megaTaigaHills = (new BiomeGenTaiga( 33, 1 )).setColor( 4542270 ).setBiomeName( "Mega Taiga Hills" ).func_76733_a( 5159473 ).setTemperatureAndRainfall( 0.3F, 0.8F ).setHeightRange( hillsRange );
	public static final CubeBiomeGenBase extremeHillsPlus = (new BiomeGenHills( 34, true )).setColor( 5271632 ).setBiomeName( "Extreme Hills+" ).setHeightRange( extremeHillsRange ).setTemperatureAndRainfall( 0.2F, 0.3F );
	public static final CubeBiomeGenBase savanna = (new BiomeGenSavanna( 35 )).setColor( 12431967 ).setBiomeName( "Savanna" ).setTemperatureAndRainfall( 1.2F, 0.0F ).setDisableRain().setHeightRange( PlainsRange );
	public static final CubeBiomeGenBase SavannaPlateau = (new BiomeGenSavanna( 36 )).setColor( 10984804 ).setBiomeName( "Savanna Plateau" ).setTemperatureAndRainfall( 1.0F, 0.0F ).setDisableRain().setHeightRange( plateauRange );
	public static final CubeBiomeGenBase mesa = (new BiomeGenMesa( 37, false, false )).setColor( 14238997 ).setBiomeName( "Mesa" );
	public static final CubeBiomeGenBase mesaPlateauF = (new BiomeGenMesa( 38, false, true )).setColor( 11573093 ).setBiomeName( "Mesa Plateau F" ).setHeightRange( plateauRange );
	public static final CubeBiomeGenBase mesaPlateau = (new BiomeGenMesa( 39, false, false )).setColor( 13274213 ).setBiomeName( "Mesa Plateau" ).setHeightRange( plateauRange );

	protected static final WorldGenDoublePlantCube worldGenDoublePlant;

	/** The tree generator. */
	protected WorldGenTreesCube worldGeneratorTrees;

	/** The big tree generator. */
	protected WorldGenBigTreeCube worldGeneratorBigTree;

	/** The swamp tree generator. */
	protected WorldGenSwampCube worldGeneratorSwamp;

	/** The average height of this biome. Default 0.1. */
	public float biomeHeight;

	/** The average volatility of this biome. Default 0.3. */
	public float biomeVolatility;

	protected CubeBiomeGenBase( int biomeID )
	{
		super( biomeID );
		this.biomeHeight = defaultBiomeRange.biomeHeight;
		this.biomeVolatility = defaultBiomeRange.biomeVolatility;

		this.worldGeneratorTrees = new WorldGenTreesCube( false );
		this.worldGeneratorBigTree = new WorldGenBigTreeCube( false );
		this.worldGeneratorSwamp = new WorldGenSwampCube();

		biomeList[biomeID] = this;
		this.theBiomeDecorator = this.createBiomeDecorator();
	}

	public CubeBiomeDecorator decorator()
	{
		return (CubeBiomeDecorator)this.theBiomeDecorator;
	}

	//some getters because these variables are "protected" and not accessible from other packages
	public boolean getEnableRain()
	{
		return this.enableRain;
	}

	protected List getSpawnableCreatureList()
	{
		return this.spawnableCreatureList;
	}

	protected List getSpawnableMonsterList()
	{
		return this.spawnableMonsterList;
	}

	protected List getSpawnableCaveCreatureList()
	{
		return this.spawnableCaveCreatureList;
	}

	protected List getSpawnableWaterCreatureList()
	{
		return this.spawnableWaterCreatureList;
	}

	/**
	 * Allocate a new CubeBiomeDecorator for this BiomeGenBase
	 */
	@Override
	protected CubeBiomeDecorator createBiomeDecorator()
	{
		return new CubeBiomeDecorator();
	}

	/**
	 * Sets the temperature and rainfall of this biome.
	 */
	protected CubeBiomeGenBase setTemperatureAndRainfall( float temp, float rainfall )
	{
		return (CubeBiomeGenBase)super.setTemperatureRainfall( temp, rainfall );
	}

	protected final CubeBiomeGenBase setHeightRange( CubeBiomeGenBase.Height range )
	{
		this.biomeHeight = range.biomeHeight;
		this.biomeVolatility = range.biomeVolatility;
		return this;
	}

	/**
	 * Disable the rain for the biome.
	 */
	@Override
	protected CubeBiomeGenBase setDisableRain()
	{
		return (CubeBiomeGenBase)super.setDisableRain();
	}

	public WorldGenAbstractTreeCube checkSpawnTree( Random rand )
	{
		return (WorldGenAbstractTreeCube)(rand.nextInt( 10 ) == 0 ? this.worldGeneratorBigTree : this.worldGeneratorTrees);
	}

	/**
	 * Gets a WorldGen appropriate for this biome.
	 */
	@Override
	public WorldGeneratorCube getRandomWorldGenForGrass( Random par1Random )
	{
		return new WorldGenTallGrassCube( Blocks.tallgrass, 1 );
	}

	public String spawnFlower( Random rand, int p_150572_2_, int p_150572_3_, int p_150572_4_ )
	{
		return rand.nextInt( 3 ) > 0 ? BlockFlower.field_149858_b[0] : BlockFlower.field_149859_a[0];
	}

	/**
	 * sets enableSnow to true during biome initialization. returns BiomeGenBase.
	 */
	@Override
	protected CubeBiomeGenBase setEnableSnow()
	{
		return (CubeBiomeGenBase)super.setEnableSnow();
	}

	@Override
	protected CubeBiomeGenBase setBiomeName( String name )
	{
		return (CubeBiomeGenBase)super.setBiomeName( name );
	}

	@Override
	protected CubeBiomeGenBase func_76733_a( int par1 )
	{
		return (CubeBiomeGenBase)super.func_76733_a( par1 );
	}

	@Override
	protected CubeBiomeGenBase setColor( int par1 )
	{
		return (CubeBiomeGenBase)super.setColor( par1 );
	}

	@Override
	protected CubeBiomeGenBase func_150563_c( int p_150563_1_ )
	{
		return (CubeBiomeGenBase)super.func_150563_c( p_150563_1_ );
	}

	@Override
	protected CubeBiomeGenBase func_150557_a( int p_150557_1_, boolean p_150557_2_ )
	{
		return (CubeBiomeGenBase)super.func_150557_a( p_150557_1_, p_150557_2_ );
	}

	public void decorate( World world, Random rand, int cubeX, int cubeY, int cubeZ )
	{
		((CubeBiomeDecorator)this.theBiomeDecorator).decorate( world, rand, this, cubeX, cubeY, cubeZ );
	}

	public void modifyBlocks_pre( World world, Random rand, Cube cube, int xAbs, int yAbs, int zAbs, double val )
	{
		this.modifyBlocks( world, rand, cube, xAbs, yAbs, zAbs, val );
	}

	public final void modifyBlocks( World world, Random rand, Cube cube, int xAbs, int yAbs, int zAbs, double val )
	{
		Block topBlock = this.topBlock; // grass/gravel/stone
		byte var11 = (byte)(this.field_150604_aj & 255);
		Block fillBlock = this.fillerBlock; // dirt/gravel/stone
		int var13 = -1;
		int rnd1 = (int)(val / 3.0D + 3.0D + rand.nextDouble() * 0.25D);

		int xRel = xAbs & 15;
		int yRel = yAbs & 15;
		int zRel = zAbs & 15;

		int seaLevel = 63; //replace with user-selectable

		/**Default BuildDepth is 8,388,608. the Earth has a radius of ~6,378,100m. Not too far off.
		 * Let's make this world similar to the earth!
		 *
		 *	Crust - 0 to 35km (varies between 5 and 70km thick due to the sea and mountains)
		 *	Upper Mesosphere - 35km to 660km
		 *	Lower Mesosphere - 660km to 2890km
		 *	Outer Core - 2890km to 5150km
		 *	Inner Core - 5150km to 6360km - apparently, the innermost sections of the core could be a plasma! Crazy!
		 */
		if( yAbs <= BuildSizeEvent.getBuildDepth() + 16 + rand.nextInt( 16 ) ) // generate bedrock in the very bottom cube and below plus random bedrock in the cube above that
		{
			cube.setBlockForGeneration( xRel, yRel, zRel, Blocks.bedrock );
		}
		else if( yAbs < -32768 + rand.nextInt( 256 ) ) // generate lava sea under y = -32768, plus a rough surface. this is pretty fucking deep though, so nobody will reach this, probably.
		{
			cube.setBlockForGeneration( xRel, yRel, zRel, Blocks.lava );
		}
		else
		{
			Block block = cube.getBlock( xRel, yRel, zRel );

			if( block != null && block.getMaterial() != Material.air )
			{
				if( block == Blocks.stone )
				{
					if( var13 == -1 )
					{
						if( rnd1 <= 0 )
						{
							topBlock = null;
							var11 = 0;
							fillBlock = Blocks.stone; // stone/stone/stone
						}
						else if( yAbs >= seaLevel - 4 && yAbs <= seaLevel + 1 )
						{
							topBlock = this.topBlock;
							var11 = (byte)(this.field_150604_aj & 255);
							fillBlock = this.fillerBlock;
						}

						if( yAbs < seaLevel && (topBlock == null || topBlock.getMaterial() == Material.air) )
						{
							if( this.getFloatTemperature( xAbs, yAbs, zAbs ) < 0.15F )
							{
								topBlock = Blocks.ice;
								var11 = 0;
							}
							else
							{
								topBlock = Blocks.water;
								var11 = 0;
							}
						}

						var13 = rnd1;

						if( yAbs >= 62 )
						{
							cube.setBlockForGeneration( xRel, yRel, zRel, topBlock, var11 ); //grass/gravel/stone
						}
						else if( yAbs < 56 - rnd1 )
						{
							topBlock = null;
							fillBlock = Blocks.stone; // stone/stone/stone

//                            cubeBlocks.setBlock(xRel, yRel, zRel, Blocks.gravel);
						}
						else
						{
							cube.setBlockForGeneration( xRel, yRel, zRel, fillBlock );
						}
					}
					else if( var13 > 0 )
					{
						--var13;
						cube.setBlockForGeneration( xRel, yRel, zRel, fillBlock );

						if( var13 == 0 && fillBlock == Blocks.sand )
						{
							var13 = rand.nextInt( 4 ) + Math.max( 0, yAbs - 63 );
							fillBlock = Blocks.sandstone;
						}
					}
				}
			}
			else
			{
				var13 = -1;
			}
		}
	}

	@Override
	protected CubeBiomeGenBase func_150566_k()
	{
		return createAndReturnMutated();
	}

	protected CubeBiomeGenBase createAndReturnMutated()
	{
		return new BiomeGenMutated( this.biomeID + 128, this );
	}

	@Override
	public Class<? extends CubeBiomeGenBase> func_150562_l()
	{
		return this.getClass();
	}

	public static CubeBiomeGenBase[] getBiomeGenArray()
	{
		return biomeList;
	}

	public static CubeBiomeGenBase getBiome( int val )
	{
		if( val >= 0 && val <= biomeList.length && biomeList[val] != null )
		{
			return biomeList[val];
		}
		else
		{
			logger.warn( "Biome ID is invalid or out of bounds: " + val + ", defaulting to 0 (Ocean)" );
			return ocean;
		}
	}

	static
	{
		plains.createAndReturnMutated();
		desert.createAndReturnMutated();
		forest.createAndReturnMutated();
		taiga.createAndReturnMutated();
		swampland.createAndReturnMutated();
		icePlains.createAndReturnMutated();
		jungle.createAndReturnMutated();
		jungleEdge.createAndReturnMutated();
		coldTaiga.createAndReturnMutated();
		savanna.createAndReturnMutated();
		SavannaPlateau.createAndReturnMutated();
		mesa.createAndReturnMutated();
		mesaPlateauF.createAndReturnMutated();
		mesaPlateau.createAndReturnMutated();
		birchForest.createAndReturnMutated();
		birchForestHills.createAndReturnMutated();
		roofedForest.createAndReturnMutated();
		megaTaiga.createAndReturnMutated();
		extremeHills.createAndReturnMutated();
		extremeHillsPlus.createAndReturnMutated();
		biomeList[megaTaigaHills.biomeID + 128] = biomeList[megaTaiga.biomeID + 128];
		CubeBiomeGenBase[] array = biomeList;
		int var1 = array.length;

		for( int var2 = 0; var2 < var1; ++var2 )
		{
			CubeBiomeGenBase biome = array[var2];

			if( biome != null && biome.biomeID < 128 )
			{
				field_150597_n.add( biome );
			}
		}

		field_150597_n.remove( hell );
		field_150597_n.remove( sky );
		worldGenDoublePlant = new WorldGenDoublePlantCube();
	}

	public static class Height
	{
		public float biomeHeight;
		public float biomeVolatility;

		public Height( float p_i45371_1_, float p_i45371_2_ )
		{
			this.biomeHeight = p_i45371_1_;
			this.biomeVolatility = p_i45371_2_;
		}

		public CubeBiomeGenBase.Height func_150775_a()
		{
			return new CubeBiomeGenBase.Height( this.biomeHeight * 0.8F, this.biomeVolatility * 0.6F );
		}
	}
}
