package io.embry.hellowordel.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.embry.hellowordel.data.WordsRepoImpl
import io.embry.hellowordel.domain.WordsRepo

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @Provides
    fun provideWordsRepo(
        @ApplicationContext context: Context
    ): WordsRepo =
        WordsRepoImpl(context = context)
}