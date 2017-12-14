package org.liamjd.cantilevers.controllers.validators

interface Validator {
	fun validate(parameter: String): Map<String,String>
//	fun validate(parameterMap: Map<String,String>): Map<String,String>
}


object BridgeNameValidator : Validator {
	override fun validate(parameter: String): Map<String, String> {
		return validateName(parameter)
	}

//	override fun validate(parameterMap: Map<String, String>): Map<String, String> {
//		return validateName(parameterMap["add-bridge-name"])
//	}

	private fun validateName(bridgeName: String?): Map<String, String> {

		val errors: MutableMap<String, String> = mutableMapOf()
		if (bridgeName == null || bridgeName.isEmpty()) {
			errors.put("name", "Name must not be blank")
		} else {
			if (bridgeName.length < 3 || bridgeName.length > 50) {
				errors.put("name", "Name must be between 3 and 50 characters long")
			}
		}

		return errors
	}
}