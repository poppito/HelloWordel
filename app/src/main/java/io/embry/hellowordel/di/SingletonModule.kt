package io.embry.hellowordel.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.embry.hellowordel.data.WordsRepoImpl
import io.embry.hellowordel.domain.WordsRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @Provides
    fun provideExternalScope() = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    fun provideWordsRepo(
        @ApplicationContext context: Context,
        externalScope: CoroutineScope
    ): WordsRepo =
        WordsRepoImpl(context = context)
}