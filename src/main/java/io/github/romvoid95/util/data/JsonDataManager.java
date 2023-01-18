package io.github.romvoid95.util.data;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonDataManager<T> implements DataManager<T>
{

    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final Logger  log  = LoggerFactory.getLogger(JsonDataManager.class);
    private final Path           configPath;
    private final T              data;
    private boolean              exitIfNotExist;

    public JsonDataManager(Class<T> clazz, String file, Supplier<T> constructor)
    {
        this(clazz, file, constructor, false);
    }

    public JsonDataManager(Class<T> clazz, String file, Supplier<T> constructor, boolean exitIfNotExist)
    {
        this.configPath = Paths.get(file);
        this.exitIfNotExist = exitIfNotExist;

        if(createDirsIfNeeded(file))
        {
            if (!configPath.toFile().exists())
            {
                log.info("Could not find config file at " + configPath.toFile().getAbsolutePath() + ", creating a new one...");
                try
                {
                    if (configPath.toFile().createNewFile())
                    {
                        log.info("Generated new config file at " + configPath.toFile().getAbsolutePath() + ".");
                        write(configPath, Mapper.prettyPrintWriter().writeValueAsString(constructor.get()));
                        log.info("Please, fill the file with valid properties.");
                    } else
                    {
                        log.warn("Could not create config file at " + file);
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                    System.exit(1);
                }

                if (this.exitIfNotExist)
                {
                    System.exit(0);
                }
            }
        }
        try
        {
            this.data = Mapper.fromJson(read(configPath), clazz);
        } catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private boolean createDirsIfNeeded(String file)
    {
        String[] split = file.split("/");        
        if (split.length > 2)
        {
            Path dirs = Paths.get(split[0], Arrays.copyOfRange(split, 1, split.length - 1));
            return dirs.toFile().mkdirs();
        } else if (split.length == 2)
        {
            Path dir = Paths.get(split[0]);
            return dir.toFile().mkdir();
        } else if (split.length == 1)
        {
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public T get()
    {
        return data;
    }

    @Override
    public void save()
    {
        try
        {
            write(configPath, Mapper.prettyPrintWriter().writeValueAsString(data));
        } catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private String read(Path path) throws IOException
    {
        return Files.readString(path, UTF8);
    }

    private void write(Path path, String contents) throws IOException
    {
        Files.writeString(path, contents, UTF8);
    }
}
