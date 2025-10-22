package com.fungoussoup.ancienthorizons.entity.client.wolverine;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class WolverineAnimations {
    public static final AnimationDefinition ANGRY = AnimationDefinition.Builder.withLength(0.0417F).looping()
            .addAnimation("ears", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.5F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();
}
