package energon.nebulalib.entity;

public interface IENebula {
    NebulaCategory getCategory();
    default boolean aggressive(NebulaCategory target) {
        return this.getCategory().aggressive(target);
    }
}
