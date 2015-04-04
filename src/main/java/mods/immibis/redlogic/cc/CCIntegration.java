package mods.immibis.redlogic.cc;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

/**
 * @author Vexatos
 */
public class CCIntegration {

	private static final CCIntegration INSTANCE = new CCIntegration();
	private Class<?> ccInterface;

	private CCIntegration() {
	}

	private void findCCTile() {
		try {
			ccInterface = Class.forName("dan200.computercraft.shared.computer.blocks.IComputerTile");
		} catch(Throwable e) {
			LogManager.getLogger("RedLogic").error("ComputerCraft detected, but unable to initialize ComputerCraft integration. This is a bug and should be reported!");
			ccInterface = null;
		}
	}

	@Optional.Method(modid = "ComputerCraft")
	private byte[] getBundledOutputSafe(World world, int x, int y, int z, int side) {
		byte[] returnValue = new byte[16];
		int ccValue = ComputerCraftAPI.getBundledRedstoneOutput(world, x, y, z, side);
		for(int i = 0; i < returnValue.length; i++) {
			returnValue[i] = (byte) (((ccValue >> i) & 1) != 0 ? 255 : 0);
		}
		return returnValue;
	}

	/**
	 * @return true if the TileEntity at the specified position is a ComputerCraft computer.
	 */
	public static boolean isCCTile(World world, int x, int y, int z) {
		return world != null && isCCTile(world.getTileEntity(x, y, z));
	}

	/**
	 * @return true if the specified TileEntity is a ComputerCraft computer.
	 */
	public static boolean isCCTile(TileEntity tile) {
		return INSTANCE.ccInterface != null && tile != null && INSTANCE.ccInterface.isInstance(tile);
	}

	/**
	 * @return The output of the Computer at the specified position, already formatted to a RedLogic-compatible byte array
	 */
	public static byte[] getBundledOutput(World world, int x, int y, int z, int side) {
		if(!active() || world == null || !isCCTile(world, x, y, z)) {
			return null;
		}
		return INSTANCE.getBundledOutputSafe(world, x, y, z, side);
	}

	/**
	 * @return The output of the specified Computer, already formatted to a RedLogic-compatible byte array
	 */
	public static byte[] getBundledOutput(TileEntity tile, int side) {
		return tile == null ? null : getBundledOutput(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, side);
	}

	/**
	 * Initialized ComputerCraft integration
	 */
	public static void initialize() {
		if(!Loader.isModLoaded("ComputerCraft")) {
			return;
		}
		if(INSTANCE.ccInterface != null) {
			return;
		}
		CCBundledRedstoneProviderRedLogic.register();
		INSTANCE.findCCTile();
	}

	/**
	 * @return true if CC Integration is active (i.e. ComputerCraft has been detected and this has been initialized)
	 */
	public static boolean active() {
		return Loader.isModLoaded("ComputerCraft") && INSTANCE.ccInterface != null;
	}
}
