===========
Error Codes
===========

Why Error Codes?
----------------

Because Javet doesn't want to support i18n though both Node.js and V8 support i18n.

* The final binary size will significantly increase with i18n.
* There is no development resource for i18n.

Backward Compatibility
----------------------

Once the error codes are released, they are likely not changed any more for backward compatibility.

The following error codes are generated automatically from `source code <../../src/main/java/com/caoccao/javet/exceptions/JavetError.java>`_.

.. Error Codes Begin


==== =========== ====================================== =================================================================================================================================================================
Code Type        Name                                   Format                                                                                                                                                           
==== =========== ====================================== =================================================================================================================================================================
101  System      OSNotSupported                         OS ${OS} is not supported                                                                                                                                        
102  System      LibraryNotFound                        Javet library ${path} is not found                                                                                                                               
103  System      LibraryNotLoaded                       Javet library is not loaded because ${reason}                                                                                                                    
104  System      NotSupported                           ${feature} is not supported                                                                                                                                      
105  System      FailedToReadPath                       Failed to read ${path}                                                                                                                                           
201  Compilation CompilationFailure                     ${message}                                                                                                                                                       
301  Execution   ExecutionFailure                       ${message}                                                                                                                                                       
302  Execution   ExecutionTerminated                    Execution is terminated and continuable is ${continuable}                                                                                                        
401  Callback    CallbackSignatureParameterSizeMismatch Callback signature mismatches: method name is ${methodName}, expected parameter size is ${expectedParameterSize}, actual parameter size is ${actualParameterSize}
402  Callback    CallbackSignatureParameterTypeMismatch Callback signature mismatches: expected parameter type is ${expectedParameterType}, actual parameter type is ${actualParameterType}                              
403  Callback    CallbackInjectionFailure               Failed to inject runtime with error message ${message}                                                                                                           
404  Callback    CallbackRegistrationFailure            Callback ${methodName} registration failed with error message ${message}                                                                                         
501  Converter   ConverterFailure                       Failed to convert values with error message ${message}                                                                                                           
601  Module      ModuleNameEmpty                        Module name is empty                                                                                                                                             
701  Lock        LockAcquisitionFailure                 Failed to acquire the lock                                                                                                                                       
702  Lock        LockReleaseFailure                     Failed to release the lock                                                                                                                                       
703  Lock        LockConflictThreadIdMismatch           Runtime lock conflict is detected with locked thread ID ${lockedThreadID} and current thread ID ${currentThreadID}                                               
801  Runtime     RuntimeAlreadyClosed                   Runtime is already closed                                                                                                                                        
802  Runtime     RuntimeAlreadyRegistered               Runtime is already registered                                                                                                                                    
803  Runtime     RuntimeNotRegistered                   Runtime is not registered                                                                                                                                        
804  Runtime     RuntimeLeakageDetected                 ${count} runtime(s) leakage is detected                                                                                                                          
==== =========== ====================================== =================================================================================================================================================================


.. Error Codes End


[`Home <../../README.rst>`_] [`Development <index.rst>`_]
