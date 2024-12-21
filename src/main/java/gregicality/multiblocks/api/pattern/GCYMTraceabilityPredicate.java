package gregicality.multiblocks.api.pattern;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;

import gregtech.api.pattern.PatternStringError;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.BlockInfo;

import gregicality.multiblocks.api.GCYMAPI;
import gregicality.multiblocks.common.block.blocks.BlockSuperconductorCoil;
import gregicality.multiblocks.common.block.blocks.BlockTieredComponent;

public class GCYMTraceabilityPredicate {

    private static final Supplier<TraceabilityPredicate> SUPERCONDUCTOR_PREDICATE = () -> new TraceabilityPredicate(
            blockWorldState -> {
                IBlockState blockState = blockWorldState.getBlockState();
                if (GCYMAPI.SUPERCONDUCTOR_COILS.containsKey(blockState)) {
                    BlockSuperconductorCoil.CasingType tier = GCYMAPI.SUPERCONDUCTOR_COILS.get(blockState);
                    Object casing = blockWorldState.getMatchContext().getOrPut("SuperconductorTier", tier);
                    if (!casing.equals(tier)) {
                        blockWorldState.setError(
                                new PatternStringError("gregtech.multiblock.pattern.error.superconductor_tier"));
                        return false;
                    }
                    blockWorldState.getMatchContext().getOrPut("VBlock", new LinkedList<>())
                            .add(blockWorldState.getPos());
                    return true;
                }
                return false;
            }, () -> GCYMAPI.SUPERCONDUCTOR_COILS.entrySet().stream()
                    .sorted(Comparator.comparingInt(entry -> entry.getValue().ordinal()))
                    .map(entry -> new BlockInfo(entry.getKey(), null))
                    .toArray(BlockInfo[]::new))
                            .addTooltips("gcym.multiblock.pattern.superconductor_tier");

    public static TraceabilityPredicate superconductorCoils() {
        return SUPERCONDUCTOR_PREDICATE.get();
    }

    private static final Supplier<TraceabilityPredicate> COMPONENT_PREDICATE = () -> new TraceabilityPredicate(
            blockWorldState -> {
                IBlockState blockState = blockWorldState.getBlockState();
                if (GCYMAPI.TIERED_COMPONENTS.containsKey(blockState)) {
                    BlockTieredComponent.CasingType tier = GCYMAPI.TIERED_COMPONENTS.get(blockState);
                    Object casing = blockWorldState.getMatchContext().getOrPut("ComponentTier", tier);
                    if (!casing.equals(tier)) {
                        blockWorldState.setError(
                                new PatternStringError("gregtech.multiblock.pattern.error.component_tier"));
                        return false;
                    }
                    blockWorldState.getMatchContext().getOrPut("VBlock", new LinkedList<>())
                            .add(blockWorldState.getPos());
                    return true;
                }
                return false;
            }, () -> GCYMAPI.TIERED_COMPONENTS.entrySet().stream()
                    .sorted(Comparator.comparingInt(entry -> entry.getValue().ordinal()))
                    .map(entry -> new BlockInfo(entry.getKey(), null))
                    .toArray(BlockInfo[]::new))
                            .addTooltips("gcym.multiblock.pattern.component_tier");

    public static TraceabilityPredicate tieredComponents() {
        return COMPONENT_PREDICATE.get();
    }
}
