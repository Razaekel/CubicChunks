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
package cubicchunks.client;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ClientChunkCache;
import net.minecraft.world.gen.IChunkGenerator;
import cubicchunks.generator.GeneratorStage;
import cubicchunks.world.ICubeCache;
import cubicchunks.world.column.BlankColumn;
import cubicchunks.world.column.Column;
import cubicchunks.world.cube.Cube;

public class ClientCubeCache extends ClientChunkCache implements ICubeCache {
	
	private World world;
	private BlankColumn blankColumn;
	
	public ClientCubeCache(World world) {
		super(world);
		
		this.world = world;
		this.blankColumn = new BlankColumn(world, 0, 0);
	}
	
	@Override
	public Column loadChunk(int cubeX, int cubeZ) {
		
		// is this chunk already loaded?
		Column column = (Column)this.cacheMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(cubeX, cubeZ));
		if (column != null) {
			return column;
		}
		
		// make a new one
		column = new Column(this.world, cubeX, cubeZ);
		
		this.cacheMap.add(ChunkCoordIntPair.chunkXZ2Int(cubeX, cubeZ), column);
		this.cachedChunks.add(column);
		
		column.setChunkLoaded(true);
		return column;
	}
	
	@Override
	public void unloadCube(int cubeX, int cubeY, int cubeZ) {
		
		// is this column loaded?
		Column column = (Column)this.cacheMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(cubeX, cubeZ));
		if (column == null) {
			return;
		}
		
		// is this cube loaded?
		if (column.getCube(cubeY) == null) {
			return;
		}
		
		// unload the cube
		column.removeCube(cubeY);
		
		// is the column empty?
		if (!column.hasCubes()) {
			this.cacheMap.remove(ChunkCoordIntPair.chunkXZ2Int(cubeX, cubeZ));
			this.cachedChunks.remove(column);
		}
	}
	
	@Override
	public Column getColumn(int columnX, int columnZ) {
		return getChunk(columnX, columnZ);
	}
	
	@Override
	public Column getChunk(int cubeX, int cubeZ) {
		
		// is this chunk already loaded?
		Column column = (Column)this.cacheMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(cubeX, cubeZ));
		if (column != null) {
			return column;
		}
		
		return this.blankColumn;
	}
	
	@Override
	public boolean cubeExists(int cubeX, int cubeY, int cubeZ) {
		// cubes always exist on the client
		return true;
	}
	
	@Override
	public Cube getCube(int cubeX, int cubeY, int cubeZ) {
		Cube cube = loadChunk(cubeX, cubeZ).getOrCreateCube(cubeY, false);
		
		// cubes are always live on the client
		cube.setGeneratorStage(GeneratorStage.getLastStage());
		
		return cube;
	}
	
	@Override
	public boolean generateOceanMonument(IChunkGenerator p0, Chunk p1, int p2, int p3) {
		// TODO Auto-generated method stub
		return false;
	}
}
