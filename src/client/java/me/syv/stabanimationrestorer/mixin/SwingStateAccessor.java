
package me.syv.stabanimationrestorer.mixin;

import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.SwingState.class)
public interface SwingStateAccessor {

    @Accessor("currentSwing")
    LivingEntity.SwingDescription
    stabanimationrestorer$getCurrentSwing();

    @Accessor("currentSwing")
    void stabanimationrestorer$setCurrentSwing(
            LivingEntity.SwingDescription swing
    );
}
