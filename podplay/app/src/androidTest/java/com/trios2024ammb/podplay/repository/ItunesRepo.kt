package com.trios2024ammb.podplay.repository;

import com.trios2024ammb.podplay.service.ItunesService;

class ItunesRepo(private val itunesService: ItunesService) {
    suspend fun searchByTerm(term: String) =
        itunesService.searchPodcastByTerm(term)
}