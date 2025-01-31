package com.zoffcc.applications.trifa;

import java.awt.*;
import java.io.File;

public class HelperOSFile {

    private static final String TAG = "trifa.Hlp.HelperOSFile";

    public static void show_containing_dir_in_explorer(final String filename_with_path)
    {
        try
        {
            String containing_dir = new File(filename_with_path).getParent();
            if (containing_dir != null)
            {
                if (!containing_dir.equals(""))
                {
                    File c_dir = new File(containing_dir);
                    if (c_dir.isDirectory())
                    {
                        Desktop.getDesktop().open(c_dir);
                    }
                }
            }
        }
        catch (Exception e2)
        {
            e2.printStackTrace();
        }
    }

    public static void show_file_in_explorer_or_open(String filename_with_path)
    {
        if (OperatingSystem.getCurrent() == OperatingSystem.WINDOWS)
        {
            final String filename_for_windows = filename_with_path.replace("/", "\\");
            try
            {
                Desktop.getDesktop().browseFileDirectory(new File(filename_for_windows));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                show_containing_dir_in_explorer(filename_for_windows);
            }
        }
        else if ((OperatingSystem.getCurrent() == OperatingSystem.LINUX) || (OperatingSystem.getCurrent() == OperatingSystem.RASPI))
        {
            if (MainActivity.getDB_PREF__open_files_directly())
            {
                try {
                    Desktop.getDesktop().open(new File(filename_with_path));
                } catch (Exception e2) {
                    e2.printStackTrace();
                    show_containing_dir_in_explorer(filename_with_path);
                }
            }
            else
            {
                try
                {
                    Desktop.getDesktop().browseFileDirectory(new File(filename_with_path));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    show_containing_dir_in_explorer(filename_with_path);
                }
            }
        }
        else if ((OperatingSystem.getCurrent() == OperatingSystem.MACOS)
                || (OperatingSystem.getCurrent() == OperatingSystem.MACARM))
        {
            try
            {
                Desktop.getDesktop().browseFileDirectory(new File(filename_with_path));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                show_containing_dir_in_explorer(filename_with_path);
            }
        }
    }
}
