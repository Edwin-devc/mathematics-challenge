<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::disableForeignKeyConstraints();

        Schema::create('participants', function (Blueprint $table) {
            $table->id('participant_id');
            $table->string('username')->unique();
            $table->string('firstname');
            $table->string('lastname');
            $table->string('email')->unique();
            $table->date('date_of_birth');
            $table->string('image_path');
            $table->string('password');
            $table->string('school_registration_number');
            $table->unsignedInteger('total_attempts')->default(0);
            $table->unsignedSmallInteger('total_challenges')->default(0);
            $table->timestamps();

            $table->foreign('school_registration_number')->references('registration_number')->on('schools')->onDelete('cascade');
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::disableForeignKeyConstraints();

        Schema::dropIfExists('participants');

        Schema::enableForeignKeyConstraints();
    }
};
