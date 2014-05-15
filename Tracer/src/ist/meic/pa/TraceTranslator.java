package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

public class TraceTranslator implements Translator {

	private final String objectTemplate = 
			"{" +
					"	$_ = $proceed($$); " +
					"	ist.meic.pa.ObjectTracer.newObject($_, $args, \"%s\", \"%s\", \"%s\");" +
					"}";

	private final String methodTemplate = 
			"{" + 
					"	ist.meic.pa.ObjectTracer.addArgumentsMethod($args, \"%s\", \"%s\", \"%s\");" +
					"	$_ = $proceed($$); " +
					"	ist.meic.pa.ObjectTracer.addReturnMethod($_, \"%s\", \"%s\", \"%s\");" +
					"}";

	private final String fieldTemplate = 
			"{" +
					"	$_ = $proceed($$); " +
					"	ist.meic.pa.ObjectTracer.addField($0, $_, $args, %s, \"%s\", \"%s\", \"%s\");" +
					"}";

	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
	CannotCompileException {
		if(TraceVMImplementation.isClassValid(className))  {
			CtClass ctClass = pool.get(className);
			TraceVMImplementation.addClientClass(className);
			Trace(ctClass);
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
	CannotCompileException {
	}

	public void Trace(CtClass ctClass) throws CannotCompileException, NotFoundException {
		for(final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			NewExprTracer(ctMethod);
			MethodCallTracer(ctMethod);
			if(TraceVMImplementation.isExtended())
				FieldAccessTracer(ctMethod);
		}
	}	

	public void FieldAccessTracer(final CtMethod ctMethod) throws CannotCompileException {
		ctMethod.instrument(new ExprEditor() {
			public void edit(FieldAccess fieldAccess) 
					throws CannotCompileException {
				try {
					if(TraceVMImplementation.isClassValid(fieldAccess.getClassName())) {
						if(!fieldAccess.getField().getType().isPrimitive()) {
							if(fieldAccess.isWriter()) {
								fieldAccess.replace(String.format(
										fieldTemplate,
										"true",
										fieldAccess.getClassName() + "." + fieldAccess.getFieldName(),
										fieldAccess.getLineNumber(), 
										fieldAccess.getFileName()));
							}
							if(fieldAccess.isReader()) {
								fieldAccess.replace(String.format(
										fieldTemplate,
										"false",
										fieldAccess.getClassName() + "." + fieldAccess.getFieldName(),
										fieldAccess.getLineNumber(), 
										fieldAccess.getFileName()));
							}
						}
					}
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void NewExprTracer(CtMethod ctMethod) throws CannotCompileException {
		ctMethod.instrument(new ExprEditor() {
			public void edit(NewExpr newExpr) 
					throws CannotCompileException {
				try {
					newExpr.replace(String.format(
							objectTemplate,
							newExpr.getConstructor().getLongName(),
							newExpr.getLineNumber(), 
							newExpr.getFileName()));
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void MethodCallTracer(final CtMethod ctMethod) throws CannotCompileException {		
		ctMethod.instrument(new ExprEditor() {
			public void edit(MethodCall methodCall) 
					throws CannotCompileException {
				if(TraceVMImplementation.isClientClass(methodCall.getClassName())) {
					try {
						methodCall.replace(String.format(methodTemplate,
								methodCall.getMethod().getLongName(),
								methodCall.getLineNumber(), 
								methodCall.getFileName(),
								methodCall.getMethod().getLongName(),
								methodCall.getLineNumber(), 
								methodCall.getFileName()));
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
