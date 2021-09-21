package com.crack;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class Ate extends AbstractJni{
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    //    private final DvmClass Native;
    private static File so = null;
    private static File apk = null;

    static {
        //防止打成jar包的时候找不到文件
        String soPath = "so/libjni-helper.so";
        String appPath = "apk/ate.apk";
//        ClassPathResource classPathResource = new ClassPathResource(soPath);
//        ClassPathResource appPathResource = new ClassPathResource(appPath);

        try {
            so = ResourceUtils.getFile("classpath:" + soPath);
            apk = ResourceUtils.getFile("classpath:" + appPath);
//            InputStream inputStream = classPathResource.getInputStream();
//            Files.copy(inputStream, Paths.get("./libjni-helper.so"), StandardCopyOption.REPLACE_EXISTING);
//            InputStream appinputStream = appPathResource.getInputStream();
//            Files.copy(appinputStream, Paths.get("./ate.apk"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Ate() {
        // 创建模拟器实例,进程名建议依照实际进程名填写，可以规避针对进程名的校验
//        emulator = AndroidEmulatorBuilder.for32Bit().addBackendFactory(new DynarmicFactory(true)).setProcessName("com.teheranvpn").build();
        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.teheranvpn").build();
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23));// 设置系统类库解析
//        vm = emulator.createDalvikVM(new File("./ate.apk")); // 创建Android虚拟机
        vm = emulator.createDalvikVM(apk); // 创建Android虚拟机
        new AndroidModule(emulator,vm).register(memory);

//        DalvikModule dm = vm.loadLibrary(new File("./libjni-helper.so"), false);
        DalvikModule dm = vm.loadLibrary(so, false);
        module = dm.getModule();// 加载好的libcms.so对应为一个模块
        vm.setJni(this);
        vm.setVerbose(true);// 设置是否打印Jni调用细节
//        dm.callJNI_OnLoad(emulator);// 手动执行JNI_OnLoad函数

        // 初始化debugger
//        Debugger debugger = emulator.attach();
        // 添加断点
//        debugger.addBreakPoint(module.base + 0x240 + 1);

        //leviathan所在的类，调用resolveClass解析该class对象
//        Native = vm.resolveClass("com/ss/sys/ces/a");
//        try {
//            Native.callStaticJniMethod(emulator, "leviathan(II[B)[B", -1, 123456, new ByteArray(vm, "".getBytes()));
//
//        } catch (Exception e) {
//
//        }
    }

    public String sha(String sum) {
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(vm.addLocalObject(new StringObject(vm, sum)));
        // 因为代码是thumb模式，别忘了+1
        Number number = module.callFunction(emulator, 0xEF560+1, list.toArray())[0];
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println(result);
        return result;

    }

    public static void main(String[] args) {
        Ate ate = new Ate();
        ate.sha("1111");
    }
}
