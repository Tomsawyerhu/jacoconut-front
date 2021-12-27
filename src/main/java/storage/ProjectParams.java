package storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 项目参数
 */
public class ProjectParams {
    public static AtomicReference<List<String>> PROJECT_SOURCE_ROOT=new AtomicReference<>(new ArrayList<>()); //项目源代码目录
    public static AtomicReference<List<String>> TEST_SOURCE_ROOT=new AtomicReference<>(new ArrayList<>()); //项目源代码目录
    public static AtomicReference<Integer> STATE=new AtomicReference<>(-1);
}
