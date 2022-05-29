package com.raywenderlich.jetnotes

import com.raywenderlich.jetnotes.data.ExternRepository
import com.raywenderlich.jetnotes.data.Repository

expect class MainViewModel(repository: Repository, cacheRepository: ExternRepository)
