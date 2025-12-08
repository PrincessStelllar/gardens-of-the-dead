package gardensofthedead.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.VerticalAnchor;

import static net.minecraft.world.level.levelgen.SurfaceRules.*;

public class ModSurfaceRules {

    private static final RuleSource
            BEDROCK = block(Blocks.BEDROCK),
            LAVA = block(Blocks.LAVA),
            GRAVEL = block(Blocks.GRAVEL),
            NETHERRACK = block(Blocks.NETHERRACK),
            SOUL_SAND = block(Blocks.SOUL_SAND),
            SOUL_SOIL = block(Blocks.SOUL_SOIL),
            NETHER_WART_BLOCK = block(Blocks.NETHER_WART_BLOCK),
            CRIMSON_NYLIUM = block(Blocks.CRIMSON_NYLIUM);

    public static RuleSource makeRules() {
        ConditionSource aboveLava = yBlockCheck(VerticalAnchor.absolute(31), 0);

        RuleSource commonRules = sequence(
                ifTrue(
                        verticalGradient(
                                "bedrock_floor",
                                VerticalAnchor.bottom(),
                                VerticalAnchor.aboveBottom(5)
                        ),
                        BEDROCK
                ),
                ifTrue(
                        not(
                                verticalGradient(
                                        "bedrock_roof",
                                        VerticalAnchor.belowTop(5),
                                        VerticalAnchor.top()
                                )
                        ),
                        BEDROCK
                ),
                ifTrue(yBlockCheck(VerticalAnchor.belowTop(5), 0), NETHERRACK),
                ifTrue(ON_FLOOR, ifTrue(not(aboveLava), ifTrue(hole(), LAVA)))
        );

        RuleSource soulblightForest = sequence(
                commonRules,
                ifTrue(
                        UNDER_CEILING,
                        sequence(
                                ifTrue(noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.3D), SOUL_SAND),
                                ifTrue(noiseCondition(Noises.NETHER_STATE_SELECTOR, -0.2D), SOUL_SOIL),
                                NETHERRACK
                        )
                ),
                ifTrue(
                        UNDER_FLOOR,
                        sequence(
                                gravelBeach(),
                                ifTrue(noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.3D), SOUL_SAND),
                                SOUL_SOIL
                        )
                ),
                NETHERRACK
        );

        RuleSource whistlingWoods = sequence(
                commonRules,
                ifTrue(ON_FLOOR,
                        ifTrue(not(noiseCondition(Noises.NETHERRACK, 0.4D)),
                                ifTrue(aboveLava,
                                        sequence(
                                                ifTrue(noiseCondition(Noises.NETHER_WART, 1.17D), NETHER_WART_BLOCK),
                                                CRIMSON_NYLIUM
                                        )
                                )
                        )
                ),
                NETHERRACK
        );

        return sequence(
                ifTrue(isBiome(ModBiomes.SOULBLIGHT_FOREST), soulblightForest),
                ifTrue(isBiome(ModBiomes.WHISTLING_WOODS), whistlingWoods)
        );
    }

    private static RuleSource gravelBeach() {
        ConditionSource above30 = yStartCheck(VerticalAnchor.absolute(30), 0);
        ConditionSource below35 = not(yStartCheck(VerticalAnchor.absolute(35), 0));
        ConditionSource patchNoise = noiseCondition(Noises.PATCH, -0.012D);
        return ifTrue(patchNoise,
                ifTrue(above30, ifTrue(below35, GRAVEL))
        );
    }

    private static RuleSource block(Block block) {
        return state(block.defaultBlockState());
    }
}
