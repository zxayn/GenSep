package com.example.generateresep.di

import android.content.Context
import androidx.room.Room
import com.example.generateresep.data.AppDatabase
import com.example.generateresep.data.NoteDao
import com.example.generateresep.data.RecipeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "generateresep_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideRecipeDao(database: AppDatabase): RecipeDao = database.recipeDao()
}
