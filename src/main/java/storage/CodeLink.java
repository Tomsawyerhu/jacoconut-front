package storage;

import storage.model.TestCaseLinking;
import storage.model.TestClassLinking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 源码文件位置对应关系
 */
public class CodeLink {
    public static AtomicReference<List<TestClassLinking>> PROJECT_TEST_CLASS_LINK=new AtomicReference<>(new ArrayList<>()); //测试类关联
    public static AtomicReference<List<TestCaseLinking>> PROJECT_TEST_CASE_LINK=new AtomicReference<>(new ArrayList<>()); //测试用例关联
}
