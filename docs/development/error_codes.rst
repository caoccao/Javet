===========
Error Codes
===========

The following error codes are generated automatically from `source code <../../src/main/java/com/caoccao/javet/exceptions/JavetError.java>`_.

.. Error Codes Begin


==== ====================================== =================================================================================================================================================================
Code Name                                   Format                                                                                                                                                           
==== ====================================== =================================================================================================================================================================
11   NotSupported                           ${feature} is not supported                                                                                                                                      
12   OSNotSupported                         OS ${OS} is not supported                                                                                                                                        
21   CompilationFailure                     ${message}                                                                                                                                                       
31   ExecutionFailure                       ${message}                                                                                                                                                       
32   ExecutionTerminated                    Execution is terminated and continuable is ${continuable}                                                                                                        
33   CallbackFailure                        ${message}                                                                                                                                                       
41   CallbackNotRegistered                  Callback is not registered                                                                                                                                       
42   CallbackSignatureParameterSizeMismatch Callback signature mismatches: method name is ${methodName}, expected parameter size is ${expectedParameterSize}, actual parameter size is ${actualParameterSize}
43   CallbackSignatureParameterTypeMismatch Callback signature mismatches: expected parameter type is ${expectedParameterType}, actual parameter type is ${actualParameterType}                              
44   CallbackMethodNotFound                 Callback method is not found with error message ${message}                                                                                                       
51   ConverterFailure                       Failed to convert values with error message ${message}                                                                                                           
61   ModuleNameEmpty                        Module name is empty                                                                                                                                             
71   LockAcquisitionFailure                 Failed to acquire the lock                                                                                                                                       
72   LockReleaseFailure                     Failed to release the lock                                                                                                                                       
73   LockConflictThreadIdMismatch           Runtime lock conflict is detected with locked thread ID ${lockedThreadID} and current thread ID ${currentThreadID}                                               
81   RuntimeAlreadyClosed                   Runtime is already closed                                                                                                                                        
82   RuntimeAlreadyRegistered               Runtime is already registered                                                                                                                                    
83   RuntimeLeakageDetected                 ${count} runtime(s) leakage is detected                                                                                                                          
84   RuntimeNotRegistered                   Runtime is not registered                                                                                                                                        
101  LibraryNotFound                        Javet library ${path} is not found                                                                                                                               
102  LibraryNotLoaded                       Javet library is not loaded because ${reason}                                                                                                                    
111  FailedToReadPath                       Failed to read ${path}                                                                                                                                           
==== ====================================== =================================================================================================================================================================


.. Error Codes End


[`Home <../../README.rst>`_] [`Development <index.rst>`_]
