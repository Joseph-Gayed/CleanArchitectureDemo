package cleanarchitecture.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.toLiveData
import cleanarchitecture.domain.entities.UserInfoDomainEntity
import cleanarchitecture.domain.usecases.GetUserInfoUseCase
import cleanarchitecture.presentation.mapper.Mapper
import cleanarchitecture.presentation.model.Resource
import cleanarchitecture.presentation.model.UserInfoPresentationModel
import cleanarchitecture.presentation.viewmodels.base.BaseVM
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.Function
import javax.inject.Inject

class UserInfoVM @Inject internal constructor(
    private val userInfoMapper: Mapper<UserInfoDomainEntity, UserInfoPresentationModel>,
    private val userInfoUseCase: GetUserInfoUseCase
) : BaseVM() {
    val userInfoResource: LiveData<Resource<UserInfoPresentationModel>>
        get() = userInfoUseCase
            .buildUseCase()   //return Observable<UserInfoDomainEntity>
            .map {
                domainEntity-> userInfoMapper.to(domainEntity) // return presentation model
            }
            .map {
                presentationModel-> Resource.success(presentationModel) // wrap with UI status
            }
            .startWith(Resource.loading())
            .onErrorResumeNext(
                Function
                {
                    Observable.just(Resource.error(it.localizedMessage))
                }
            ).toFlowable(BackpressureStrategy.LATEST)
            .toLiveData()
}