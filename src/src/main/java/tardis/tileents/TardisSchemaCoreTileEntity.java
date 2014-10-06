package tardis.tileents;

import java.util.ArrayList;
import java.util.List;

import tardis.api.IScrewable;
import tardis.api.TardisScrewdriverMode;
import tardis.core.Helper;
import tardis.core.TardisOutput;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TardisSchemaCoreTileEntity extends TardisAbstractTileEntity implements IScrewable
{
	private String name = null;
	private int[] bounds; 
	private int facing;

	public TardisSchemaCoreTileEntity()
	{
	}
	
	public void setData(String passedName, int[] moddedBounds, int passedFacing)
	{
		name = passedName;
		bounds = moddedBounds;
		facing = passedFacing;
	}
	
	private AxisAlignedBB getBoundingBox()
	{
		try
		{
			AxisAlignedBB retVal = AxisAlignedBB.getBoundingBox(xCoord-bounds[0],yCoord,zCoord-bounds[1],xCoord+1+bounds[2],yCoord+1+bounds[4],zCoord+1+bounds[3]);
			return retVal;
		}
		catch(Exception e)
		{
			TardisOutput.print("TSCTE","Exception " + e.getMessage(),TardisOutput.Priority.ERROR);
			e.printStackTrace();
		}
		return null;
	}
	
	private List<Entity> entitiesWithinRoom()
	{
		ArrayList<Entity> returnList = new ArrayList<Entity>();
		AxisAlignedBB boundBox = getBoundingBox();
		if(boundBox != null)
		{
			List tempList = worldObj.getEntitiesWithinAABBExcludingEntity(null, boundBox);
			if(tempList != null)
			{
				for(Object o: tempList)
				{
					if(o instanceof Entity)
						returnList.add((Entity) o);
				}
			}
		}
		return returnList;
	}
	
	public void remove()
	{
		if(name != null)
		{
			List<Entity> ents = entitiesWithinRoom();
			for(Entity ent: ents)
				Helper.teleportEntityToSafety(ent);
			TardisOutput.print("TSCTE","Removing:" + (xCoord - bounds[0]) + "to" + (xCoord + bounds[2])+ ","+(zCoord-bounds[1])+"to"+(zCoord+bounds[3]));
			for(int x = (xCoord-bounds[0]);x<=xCoord+bounds[2];x++)
				for(int y = yCoord;y<=yCoord+bounds[4];y++)
					for(int z = (zCoord-bounds[1]);z<=zCoord+bounds[3];z++)
					{
						if(x != xCoord || y != yCoord || z != zCoord)
							worldObj.setBlockToAir(x, y, z);
					}
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public boolean screw(TardisScrewdriverMode mode, EntityPlayer player)
	{
		if(worldObj.isRemote)
			return true;
		TardisCoreTileEntity core = Helper.getTardisCore(worldObj.provider.dimensionId);
		if(core == null || core.getOwner().equals(player.username))
		{
			if(mode.equals(TardisScrewdriverMode.Dismantle))
			{
				if(core.addRoom(true))
				{
					remove();
					return true;
				}
			}
		}
		else
		{
			player.addChatMessage("You do not own this TARDIS");
		}
		return false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if(name != null)
		{
			nbt.setString("name",name);
			nbt.setIntArray("bounds", bounds);
			nbt.setInteger("facing", facing);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if(nbt.hasKey("name"))
		{
			name = nbt.getString("name");
			bounds = nbt.getIntArray("bounds");
			facing = nbt.getInteger("facing");
		}
	}

}