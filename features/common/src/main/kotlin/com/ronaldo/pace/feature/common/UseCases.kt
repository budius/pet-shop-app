package com.ronaldo.pace.feature.common

import kotlinx.coroutines.flow.Flow

// A collection of reusable use cases typealias

/** one input one output */
typealias UseCase<INPUT, OUTPUT> = (INPUT) -> OUTPUT
/** no inputs, one output */
typealias SupplierCase<OUTPUT> = () -> OUTPUT
/** suspended one input one output */
typealias AsyncUseCase<INPUT, OUTPUT> = suspend (INPUT) -> OUTPUT
/** suspended no inputs, one output */
typealias AsyncSupplierCase<OUTPUT> = suspend () -> OUTPUT
/** one input, one flow of output */
typealias FlowUseCase<INPUT, OUTPUT> = (INPUT) -> Flow<OUTPUT>
/** no inputs, one flow of output */
typealias FlowSupplier<OUTPUT> = () -> Flow<OUTPUT>