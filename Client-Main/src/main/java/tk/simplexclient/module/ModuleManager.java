package tk.simplexclient.module;

import tk.simplexclient.api.module.IModuleHandler;
import tk.simplexclient.api.module.Module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager implements IModuleHandler {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
    }

    @Override
    public void registerModule(Module module) {
        this.modules.add(module);
    }

    @Override
    public void registerModules(Module... modules) {
        Collections.addAll(this.modules, modules);
    }

    @Override
    public List<Module> getModules() {
        return modules;
    }

    @Override
    public List<Module> getEnabledModules() {
        return null;
    }

    @Override
    public List<Module> getDisabledModules() {
        return null;
    }

}
