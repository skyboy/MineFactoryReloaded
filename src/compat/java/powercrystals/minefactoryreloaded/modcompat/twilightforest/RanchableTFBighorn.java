
package powercrystals.minefactoryreloaded.modcompat.twilightforest;

import net.minecraft.entity.EntityLivingBase;

import powercrystals.minefactoryreloaded.farmables.ranchables.RanchableSheep;

public class RanchableTFBighorn extends RanchableSheep {

	private Class<? extends EntityLivingBase> _tfBighornClass;

	@SuppressWarnings("unchecked")
	RanchableTFBighorn(Class<?> tfBighornClass) {

		_tfBighornClass = (Class<? extends EntityLivingBase>) tfBighornClass;
	}

	@Override
	public Class<? extends EntityLivingBase> getRanchableEntity() {

		return _tfBighornClass;
	}

}

