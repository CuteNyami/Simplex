package tk.simplexclient.api.module;

import java.util.List;

public interface IModuleHandler {

    /**
     * Register a Module
     *
     * @param module The module that you want to register
     */
    void registerModule(Module module);

    /**
     * Register multiple Modules at once
     *
     * @param modules The given Modules
     */
    void registerModules(Module... modules);

    /**
     * Get all registered Modules
     *
     * @return The registered Modules
     */
    List<Module> getModules();

    /**
     * Get all enabled registered Modules
     *
     * @return The modules that are enabled
     */
    List<Module> getEnabledModules();

    /**
     * Get all disabled registered Modules
     *
     * @return The modules that are disabled
     */
    List<Module> getDisabledModules();

}
