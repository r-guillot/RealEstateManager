package com.openclassrooms.realestatemanager.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.realestatemanager.model.Property

@Database(entities = [Property::class], version = 7)
@TypeConverters(Converters::class)
abstract class RealEstateDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var INSTANCE: RealEstateDatabase? = null

        fun getDatabase(context: Context): RealEstateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                        RealEstateDatabase::class.java,
                        "real_estate_database")
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                instance
            }
        }
    }
}