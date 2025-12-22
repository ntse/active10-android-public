package com.flipsidegroup.active10.data.persistance

import io.realm.RealmMigration


class MigrationFactory {

    fun getMigrationWithIndex(index: Int): RealmMigration {
        return arrayOf(
            Migration1To2(),
            Migration2To3(),
            Migration3To4(),
            Migration4To5(),
            Migration5To6(),
            Migration6To7(),
            Migration7to8(),
            Migration8to9(),
            Migration9to10(),
            Migration10to11(),
            Migration11to12(),
            Migration12to13(),
            Migration13to14(),
            Migration14to15(),
            Migration15to16(),
            Migration16to17(),
        )[index]
    }
}