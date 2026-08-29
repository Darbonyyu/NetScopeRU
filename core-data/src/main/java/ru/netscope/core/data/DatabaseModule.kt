package ru.netscope.core.data
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.netscope.core.data.db.MeasurementDao
import ru.netscope.core.data.db.NetScopeDatabase
import ru.netscope.core.telephony.CellDataCollector
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
 @Provides @Singleton fun provideDatabase(@ApplicationContext context: Context): NetScopeDatabase = Room.databaseBuilder(context, NetScopeDatabase::class.java, "netscope.db").build()
 @Provides fun provideMeasurementDao(database: NetScopeDatabase): MeasurementDao = database.measurementDao()
 @Provides @Singleton fun provideCellDataCollector(@ApplicationContext context: Context): CellDataCollector = CellDataCollector(context)
}
