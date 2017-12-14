package org.liamjd.cantilevers.controllers.validators

interface Validator {
	fun validate(paramater: String): Map<String,String>
}


object BridgeNameValidator : Validator {
	override fun validate(parameter: String): Map<String, String> {
		val bridgeName = parameter
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

	fun validateName(bridgeName: String?): Map<String, String> {

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