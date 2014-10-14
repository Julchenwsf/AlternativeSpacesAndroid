package groupaltspaces.alternativespacesandroid.tasks;

import java.util.List;

public interface Callback {
    public void onSuccess();

    public void onFail(List<String> message);
}
