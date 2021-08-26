package org.snowcorp.plugins.torch.impl

abstract class BaseTorch {

    abstract fun turnOn()
    abstract fun turnOff()
    abstract fun hasTorch() : Boolean
    abstract fun dispose()

}