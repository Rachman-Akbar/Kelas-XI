package com.komputerkit.stateexample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StateTestViewModel : ViewModel() {
    
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name
    
    private val _surname = MutableLiveData<String>()
    val surname: LiveData<String> = _surname
    
    init {
        _name.value = ""
        _surname.value = ""
    }
    
    fun onNameUpdate(newName: String) {
        _name.value = newName
    }
    
    fun onSurnameUpdate(newSurname: String) {
        _surname.value = newSurname
    }
}