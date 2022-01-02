package storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectParams {
    public static AtomicReference<String> PROJECT_ROOT=new AtomicReference<>();
    public static AtomicReference<List<String>> PROJECT_SOURCE_ROOT=new AtomicReference<>(new ArrayList<>());
    public static AtomicReference<List<String>> TEST_SOURCE_ROOT=new AtomicReference<>(new ArrayList<>());
    public static AtomicReference<Integer> STATE=new AtomicReference<>(-1);
}
