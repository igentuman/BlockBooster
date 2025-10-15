package igentuman.blockbooster.tile;

public interface ITileBooster {
    void setIndexStatus(long id, boolean val);
    void tickClient();
    void tickServer();
}
