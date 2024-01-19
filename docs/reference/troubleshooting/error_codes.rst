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

The following error codes are generated automatically from the :extsource3:`source code <../../../src/main/java/com/caoccao/javet/exceptions/JavetError.java>`.

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
405  Callback    CallbackMethodFailure                  Callback ${methodName} failed with error message ${message}                                                                                                      
406  Callback    CallbackUnknownFailure                 Callback failed with unknown error message ${message}                                                                                                            
407  Callback    CallbackUnregistrationFailure          Callback ${methodName} unregistration failed with error message ${message}                                                                                       
408  Callback    CallbackTypeNotSupported               Callback type ${callbackType} is not supported                                                                                                                   
501  Converter   ConverterFailure                       Failed to convert values with error message ${message}                                                                                                           
502  Converter   ConverterCircularStructure             Circular structure is detected with max depth ${maxDepth} reached                                                                                                
503  Converter   ConverterSymbolNotBuiltIn              ${symbol} is not a built-in symbol                                                                                                                               
601  Module      ModuleNameEmpty                        Module name is empty                                                                                                                                             
602  Module      ModuleNotFound                         Module ${moduleName} is not found                                                                                                                                
603  Module      ModulePermissionDenied                 Denied access to module ${moduleName}                                                                                                                            
701  Lock        LockAcquisitionFailure                 Failed to acquire the lock                                                                                                                                       
702  Lock        LockReleaseFailure                     Failed to release the lock                                                                                                                                       
703  Lock        LockConflictThreadIdMismatch           Runtime lock conflict is detected with locked thread ID ${lockedThreadID} and current thread ID ${currentThreadID}                                               
801  Runtime     RuntimeAlreadyClosed                   Runtime is already closed                                                                                                                                        
802  Runtime     RuntimeAlreadyRegistered               Runtime is already registered                                                                                                                                    
803  Runtime     RuntimeNotRegistered                   Runtime is not registered                                                                                                                                        
804  Runtime     RuntimeLeakageDetected                 ${count} runtime(s) leakage is detected                                                                                                                          
805  Runtime     RuntimeCloseFailure                    Failed to close the runtime with error message ${message}                                                                                                        
806  Runtime     RuntimeOutOfMemory                     Runtime is out of memory because ${message} with ${heapStatistics}                                                                                               
807  Runtime     RuntimeCreateSnapshotDisabled          Runtime create snapshot is disabled                                                                                                                              
808  Runtime     RuntimeCreateSnapshotBlocked           Runtime create snapshot is blocked because of ${callbackContextCount} callback context(s), ${referenceCount} reference(s), ${v8ModuleCount} module(s)            
901  Engine      EngineNotAvailable                     Engine is not available.                                                                                                                                         
==== =========== ====================================== =================================================================================================================================================================


.. Error Codes End

