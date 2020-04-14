package de.mhus.lib.core.config;

import java.io.File;
import java.net.URL;

import de.mhus.lib.annotations.activator.DefaultImplementation;
import de.mhus.lib.errors.MException;

@DefaultImplementation(DefaultConfigFactory.class)
public interface IConfigFactory {

    IConfig read(Class<?> owner, String fileName) throws MException;

    IConfig read(File file) throws MException;

    IConfig read(URL url) throws MException;
    
    IConfig create();

    void write(IConfig config, File file) throws MException;

    IConfigBuilder getBuilder(String ext);

    /**
     * This will search a file with different file extensions
     *
     * @param path Path to file without file extension
     * @return The config object or null
     * @throws MException
     */
    default public IConfig find(String path) throws MException {
        File f = new File(path);
        return find(f.getParentFile(), f.getName());
    }

    /**
     * This will search a file with different file extensions
     *
     * @param parent
     * @param name Name of file without file extension
     * @return The config object or null
     * @throws MException
     */
    default public IConfig find(File parent, String name) throws MException {
        {
            File f = new File(parent, name + ".xml");
            if (f.exists() && f.isFile()) read(f);
        }
        {
            File f = new File(parent, name + ".json");
            if (f.exists() && f.isFile()) read(f);
        }
        {
            File f = new File(parent, name + ".properties");
            if (f.exists() && f.isFile()) read(f);
        }
        {
            File f = new File(parent, name);
            if (f.exists() && f.isDirectory()) read(f);
        }
        return null;
    }

}
