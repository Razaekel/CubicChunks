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

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;

public class BiomeGenEnd extends CubeBiomeGenBase
{
	public BiomeGenEnd( int id )
	{
		super( id );
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.spawnableMonsterList.add( new CubeBiomeGenBase.SpawnListEntry( EntityEnderman.class, 10, 4, 4 ) );
		this.topBlock = Blocks.dirt;
		this.fillerBlock = Blocks.dirt;
		this.theBiomeDecorator = new BiomeEndDecorator();
	}

	/**
	 * takes temperature, returns color
	 * @param par1
	 */
	public int getSkyColorByTemp( float par1 )
	{
		return 0;
	}
}
