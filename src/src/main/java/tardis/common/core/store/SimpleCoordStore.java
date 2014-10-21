package tardis.common.core.store;

import tardis.common.core.Helper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SimpleCoordStore
{
	private final World worldObj;
	public final int world;
	public final int x;
	public final int y;
	public final int z;
	
	public SimpleCoordStore(TileEntity te)
	{
		world = te.worldObj.provider.dimensionId;
		worldObj = te.worldObj;
		x = te.xCoord;
		y = te.yCoord;
		z = te.zCoord;
	}
	
	public SimpleCoordStore(int win, int xin, int yin, int zin)
	{
		world = win;
		x = xin;
		y = yin;
		z = zin;
		worldObj = Helper.getWorld(world);
	}
	
	public SimpleCoordStore(World w, int xin, int yin, int zin)
	{
		world = w.provider.dimensionId;
		worldObj = w;
		x = xin;
		y = yin;
		z = zin;
	}
	
	public World getWorldObj()
	{
		return worldObj;
	}
	
	@Override
	public String toString()
	{
		return "World "+world + ":" + x+ ","+ y+ ","+ z;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other != null && other instanceof SimpleCoordStore)
		{
			SimpleCoordStore o = (SimpleCoordStore)other;
			if(o.world == world && o.x == x && o.y == y && o.z == z)
				return true;
		}
		return false;
	}
}
