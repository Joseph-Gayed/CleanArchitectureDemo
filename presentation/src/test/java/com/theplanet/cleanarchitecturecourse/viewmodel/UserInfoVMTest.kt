package com.theplanet.cleanarchitecturecourse.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.theplanet.cleanarchitecturecourse.domain.repository.SimpleRepository
import com.theplanet.cleanarchitecturecourse.domain.usecases.GetUserInfoUseCase
import com.theplanet.cleanarchitecturecourse.mapper.UserInfoMapperFromDomainToPresentation
import com.theplanet.cleanarchitecturecourse.model.Status
import com.theplanet.cleanarchitecturecourse.util.TestDataGenerator
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class UserInfoVMTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: SimpleRepository

    private lateinit var userInfoVM: UserInfoVM
    private val userInfoMapperFromDomainToPresentation = UserInfoMapperFromDomainToPresentation()

    private val presentationModel = TestDataGenerator.generateUserInfo()
    private val domainEntity = userInfoMapperFromDomainToPresentation.from(presentationModel)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        val getUserInfoUseCase = GetUserInfoUseCase(
            repository,
            Schedulers.trampoline(),
            Schedulers.trampoline()
        )

        userInfoVM = UserInfoVM(
            presentationModel.accountNumber,
            userInfoMapperFromDomainToPresentation,
            getUserInfoUseCase
        )
    }

    @Test
    fun test_getUserInfo_success() {

        Mockito.`when`(repository.getUserInfo(anyString()))
            .thenReturn(Observable.just(domainEntity))

        val userInfoResource = userInfoVM.userInfoResource

        userInfoResource.observeForever { /*Do nothing*/ }

        assertTrue(
            userInfoResource.value?.status == Status.SUCCESS
                    && userInfoResource.value?.data == presentationModel
        )
    }

    @Test
    fun test_getUserInfo_error() {
        val errorMsg = "user info error in fetching data"
        Mockito.`when`(repository.getUserInfo(anyString()))
            .thenReturn(Observable.error(Throwable(errorMsg)))

        val userInfoResource = userInfoVM.userInfoResource

        userInfoResource.observeForever { /*Do nothing*/ }

        assertTrue(
            userInfoResource.value?.status == Status.ERROR
                    && userInfoResource.value?.message == errorMsg
        )
    }

}