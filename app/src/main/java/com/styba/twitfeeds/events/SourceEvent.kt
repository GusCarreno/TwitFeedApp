package com.styba.twitfeeds.events

import com.styba.twitfeeds.models.Source

class SourceEvent(val position: Int, val enabled: Boolean, val source: Source)