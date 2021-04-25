===========
Error Codes
===========

The following error codes are generated automatically from `source code <../../src/main/java/com/caoccao/javet/exceptions/JavetError.java>`_.

.. Error Codes Begin


==== =========== ====================================== =================================================================================================================================================================
Code Type        Name                                   Format                                                                                                                                                           
==== =========== ====================================== =================================================================================================================================================================
11   System      NotSupported                           ${feature} is not supported                                                                                                                                      
12   System      OSNotSupported                         OS ${OS} is not supported                                                                                                                                        
13   System      LibraryNotFound                        Javet library ${path} is not found                                                                                                                               
14   System      LibraryNotLoaded                       Javet library is not loaded because ${reason}                                                                                                                    
15   System      FailedToReadPath                       Failed to read ${path}                                                                                                                                           
21   Compilation CompilationFailure                     ${message}                                                                                                                                                       
31   Execution   ExecutionFailure                       ${message}                                                                                                                                                       
32   Execution   ExecutionTerminated                    Execution is terminated and continuable is ${continuable}                                                                                                        
41   Callback    CallbackSignatureParameterSizeMismatch Callback signature mismatches: method name is ${methodName}, expected parameter size is ${expectedParameterSize}, actual parameter size is ${actualParameterSize}
42   Callback    CallbackSignatureParameterTypeMismatch Callback signature mismatches: expected parameter type is ${expectedParameterType}, actual parameter type is ${actualParameterType}                              
43   Callback    CallbackMethodNotFound                 Callback method is not found with error message ${message}                                                                                                       
44   Callback    CallbackInjectionFailure               Failed to inject runtime with error message ${message}                                                                                                           
45   Callback    CallbackRegistrationFailure            Callback ${methodName} registration failed with error message ${message}                                                                                         
51   Converter   ConverterFailure                       Failed to convert values with error message ${message}                                                                                                           
61   Module      ModuleNameEmpty                        Module name is empty                                                                                                                                             
71   Lock        LockAcquisitionFailure                 Failed to acquire the lock                                                                                                                                       
72   Lock        LockReleaseFailure                     Failed to release the lock                                                                                                                                       
73   Lock        LockConflictThreadIdMismatch           Runtime lock conflict is detected with locked thread ID ${lockedThreadID} and current thread ID ${currentThreadID}                                               
81   Runtime     RuntimeAlreadyClosed                   Runtime is already closed                                                                                                                                        
82   Runtime     RuntimeAlreadyRegistered               Runtime is already registered                                                                                                                                    
83   Runtime     RuntimeLeakageDetected                 ${count} runtime(s) leakage is detected                                                                                                                          
84   Runtime     RuntimeNotRegistered                   Runtime is not registered                                                                                                                                        
==== =========== ====================================== =================================================================================================================================================================


.. Error Codes End


[`Home <../../README.rst>`_] [`Development <index.rst>`_]
