package igentuman.blockbooster.util;

/**
 * Accessor interface for block entity tick timing data.
 * Implemented by TickingBlockEntityMixin to provide access to injected fields.
 */
public interface BlockEntityTickDataAccessor {
    
    /**
     * Get the average tick time for this block entity in milliseconds
     */
    double world_balance$getAvgTickTime();
    
    /**
     * Set the average tick time for this block entity in milliseconds
     */
    void world_balance$setAvgTickTime(double time);
    
    /**
     * Get the total number of ticks recorded for this block entity
     */
    long world_balance$getTickCount();
    
    /**
     * Set the total number of ticks recorded for this block entity
     */
    void world_balance$setTickCount(long count);
}