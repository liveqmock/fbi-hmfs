package dep.gateway.hmb8583.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-10
 * Time: ÉÏÎç11:58
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class PackageUtil {
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        List<String> cls = getClassInPackage("dep.hmfs.online.hmb.domain");
        for (String s : cls) {
            System.out.println(s);
        }

    }

    public static List<String> getClassInPackage(String pkgName) {
        List<String> ret = new ArrayList<String>();
        String rPath = pkgName.replace(".", "/") + "/";
        try {
            for (File classPath : CLASS_PATH_ARRAY) {
                if (!classPath.exists()) continue;
                if (classPath.isDirectory()) {
                    File dir = new File(classPath, rPath);
                    if (!dir.exists()) continue;
                    for (File file : dir.listFiles()) {
                        if (file.isFile()) {
                            String clsName = file.getName();
                            clsName = pkgName + ". " + clsName.substring(0, clsName.length() - 6);
                            ret.add(clsName);
                        }
                    }
                } else {
                    FileInputStream fis = new FileInputStream(classPath);
                    JarInputStream jis = new JarInputStream(fis, false);
                    JarEntry e = null;
                    while ((e = jis.getNextJarEntry()) != null) {
                        String eName = e.getName();
                        if (eName.startsWith(rPath) && !eName.endsWith("/ ")) {
                            ret.add(eName.replace(".", "/").substring(0, eName.length() - 6));
                        }
                        jis.closeEntry();
                    }
                    jis.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    private static String[] CLASS_PATH_PROP = {"java.class.path ", "java.ext.dirs ", "sun.boot.class.path "};

    private static List<File> CLASS_PATH_ARRAY = getClassPath();

    private static List<File> getClassPath() {
        List<File> ret = new ArrayList<File>();
        String delim = ":";
        if (System.getProperty("os.name").indexOf("Windows") != -1)
            delim = ";";
        for (String pro : CLASS_PATH_PROP) {
            String[] pathes = System.getProperty(pro).split(delim);
            for (String path : pathes)
                ret.add(new File(path));
        }
        return ret;
    }
}
