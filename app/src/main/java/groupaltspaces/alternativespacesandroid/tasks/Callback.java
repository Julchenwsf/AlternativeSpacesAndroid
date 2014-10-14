package groupaltspaces.alternativespacesandroid.tasks;

import java.util.List;

/**
 * Created by BrageEkroll on 14.10.2014.
 */
public interface Callback {

    public void onSuccess();

    public void onFail(List<String> message);
}
