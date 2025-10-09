package igentuman.blockbooster.tile;

public interface ITileBooster {
    void setIndexStatus(int id, byte val);
    void tickClient();
    void tickServer();
}
