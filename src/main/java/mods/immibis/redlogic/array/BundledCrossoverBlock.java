package mods.immibis.redlogic.array;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mods.immibis.core.api.multipart.util.BlockMultipartBase;
import mods.immibis.redlogic.RedLogicMod;

public class BundledCrossoverBlock extends BlockMultipartBase {

	public BundledCrossoverBlock() {
		super(RedLogicMod.circuitMaterial);
		
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new BundledCrossoverTile();
	}

	@Override
	public void onNeighborBlockChange(World w, int x, int y, int z, net.minecraft.block.Block p_149695_5_) {
		BundledCrossoverTile tile = (BundledCrossoverTile)w.getTileEntity(x, y, z);
		
		if(!BundledCrossoverTile.checkCanStay(w, x, y, z, tile.getSide())) {
			if(tile.getCoverSystem() != null)
				tile.getCoverSystem().convertToContainerBlock();
			else
				w.setBlockToAir(x, y, z);
			dropBlockAsItem(w, x, y, z, new ItemStack(this));
			return;
		}
	}
}
