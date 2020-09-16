package br.com.unb.smms.di

import android.content.Context
import br.com.unb.smms.interactor.IgInteractor
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.interactor.UserInteractor
import br.com.unb.smms.repository.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object SmmsModule {

    @Singleton
    @Provides
    fun provideUserRetrofit(@ApplicationContext context: Context) = SmmsRetrofit(context).retrofitUser

    @Singleton
    @Provides
    fun providePageRetrofit(@ApplicationContext context: Context) = SmmsRetrofit(context).retrofitPage

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideUserRepository(facebookService: FacebookService) =
        UserRepository(facebookService)

    @Singleton
    @Provides
    fun providePageRepository(facebookService: FacebookPageService) =
        PageRepository(facebookService)

    @Singleton
    @Provides
    fun provideIgRepository(igService: IgService) =
        IgRepository(igService)

    @Provides
    fun provideUserService(smmsRetrofit: SmmsRetrofit): FacebookService =
        smmsRetrofit.retrofitUser.create(FacebookService::class.java)

    @Provides
    fun providePageService(smmsRetrofit: SmmsRetrofit): FacebookPageService =
        smmsRetrofit.retrofitPage.create(FacebookPageService::class.java)

    @Provides
    fun provideIgService(smmsRetrofit: SmmsRetrofit): IgService =
        smmsRetrofit.retrofitPage.create(IgService::class.java)

    @Singleton
    @Provides
    fun provideUserInteractor(repository: UserRepository, @ApplicationContext context: Context) =
        UserInteractor(repository, context)

    @Singleton
    @Provides
    fun providePageInteractor(repository: PageRepository, @ApplicationContext context: Context) =
        PageInteractor(repository, context)

    @Singleton
    @Provides
    fun provideIgInteractor(repository: IgRepository, @ApplicationContext context: Context) =
        IgInteractor(repository, context)



}