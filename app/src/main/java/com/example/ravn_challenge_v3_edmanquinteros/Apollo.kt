package com.example.ravn_challenge_v3_edmanquinteros

import com.apollographql.apollo3.ApolloClient

val apolloClient= ApolloClient.Builder()
    .serverUrl("https://swapi-graphql.netlify.app/.netlify/functions/index")
    .build()