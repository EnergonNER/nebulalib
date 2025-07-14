package energon.nebulalib.entity;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class NebulaCategory {
    public final String name;
    public final int categoryId;
    public List<NebulaType> entities = new ArrayList<>();
    public Integer[] targetsCategory = new Integer[]{};
    public Integer[] friendsCategory = new Integer[]{};
    public NebulaCategory(String name, int id) {
        this.name = name;
        this.categoryId = id;
    }

    public void addTargets(Integer[] targets) {
        this.targetsCategory = ArrayUtils.addAll(this.targetsCategory, targets);
    }

    public void addFriends(Integer[] friends) {
        this.friendsCategory = ArrayUtils.addAll(this.friendsCategory, friends);
    }

    public boolean aggressive(NebulaCategory target) {
        int targetId = target.categoryId;
        if (targetId == this.categoryId) {
            return false;
        }
        for (Integer id : this.friendsCategory) {
            if (id == targetId) {
                return false;
            }
        }
        for (Integer id : this.targetsCategory) {
            if (id == targetId) {
                return true;
            }
        }
        return false;
    }
}
