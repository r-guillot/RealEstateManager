package com.openclassrooms.realestatemanager.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.model.Property
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Property::class], version = 1)
abstract class RealEstateDatabase: RoomDatabase() {
    abstract fun  propertyDao(): PropertyDao

    private class PropertyDatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { realEstateDatabase ->
                scope.launch { var propertyDao = realEstateDatabase.propertyDao()
                    propertyDao.deleteAllProperty()

                    var property = Property(1, address = "2 promenade louis lachenal", sold = false,
                            arrivalDate = "15/03/2021", agent = "robin",
                            type = null, photo = null, pointOfInterest = null, price = null,
                            description = "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...",
                    surface = null, room = 5, bathroom = null, bedroom = null, asset = null, soldDate = null)
                    propertyDao.insertProperty(property)
                }
            }
        }
    }

    companion object {
         @Volatile
         private var INSTANCE: RealEstateDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RealEstateDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                RealEstateDatabase::class.java,
                "real_estate_database")
                        .addCallback(PropertyDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                instance
            }
        }
    }
}