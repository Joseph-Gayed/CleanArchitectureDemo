package cleanarchitecture.data.repository

import cleanarchitecture.data.model.UserInfoDataModel
import io.reactivex.Observable

interface LocalDataSource {
    fun getUserInfo(): Observable<UserInfoDataModel>
    fun saveUserInfo(userInfoDataModel: UserInfoDataModel)
}