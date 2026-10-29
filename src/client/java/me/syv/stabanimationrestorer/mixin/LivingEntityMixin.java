package me.syv.stabanimationrestorer.mixin;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    @Final
    private LivingEntity.SwingState swingState;

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity$SwingState;tick()V"))
    private void stabanimationrestorer$restoreLegacySwing(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof Player)) return;

        SwingStateAccessor state = (SwingStateAccessor) this.swingState;

        LivingEntity.SwingDescription swing = state.stabanimationrestorer$getCurrentSwing();

        if (swing == null) return;
        if (swing.animation().type() != SwingAnimationType.STAB) return;

        ItemStack heldItem = entity.getItemInHand(swing.hand());
        if (heldItem.is(ItemTags.SPEARS)) return;

        SwingAnimation animation = heldItem.getAttackAnimation();

        // Recreate the duration calculation used by the old LivingEntity
        int duration = animation.duration();

        if (net.minecraft.world.effect.MobEffectUtil.hasDigSpeed(entity)) {
            duration -= 1
                    + net.minecraft.world.effect.MobEffectUtil
                    .getDigSpeedAmplification(entity);
        } else {
            MobEffectInstance fatigue =
                    entity.getEffect(MobEffects.MINING_FATIGUE);

            if (fatigue != null) duration += (1 + fatigue.getAmplifier()) * 2;
        }

        duration = Math.max(1, duration);

        // Update the existing swing without restarting it
        // SwingState.tick() will calculate progress using the new duration
        state.stabanimationrestorer$setCurrentSwing(
                new LivingEntity.SwingDescription(
                        swing.hand(),
                        animation,
                        duration
                )
        );
    }
}
