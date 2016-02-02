package co.touchlab.researchstack.sampleapp;

import android.content.Context;

import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.storage.file.SimpleFileAccess;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.glue.AppPrefs;
import co.touchlab.researchstack.glue.DataProvider;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.ResourceManager;
import co.touchlab.researchstack.glue.UiManager;

public class SampleResearchStack extends ResearchStack
{

    @Override
    protected AppDatabase createAppDatabaseImplementation(Context context)
    {
        return DatabaseHelper.getInstance(context);
    }

    @Override
    protected FileAccess createFileAccessImplementation(Context context)
    {
        return new SimpleFileAccess();
    }

    @Override
    protected PinCodeConfig getPinCodeConfig(Context context)
    {
        long autoLockTime = AppPrefs.getInstance(context).getAutoLockTime();
        return new PinCodeConfig(autoLockTime);
    }

    @Override
    protected ResourceManager createResourceManagerImplementation(Context context)
    {
        return new SampleResourceManager();
    }

    @Override
    protected UiManager createUiManagerImplementation(Context context)
    {
        return new SampleUiManager();
    }

    @Override
    protected DataProvider createDataProviderImplementation(Context context)
    {
        return new SampleDataProvider();
    }

}